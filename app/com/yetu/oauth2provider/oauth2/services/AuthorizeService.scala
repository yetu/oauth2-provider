package com.yetu.oauth2provider
package oauth2
package services

import java.net.URLDecoder

import com.yetu.oauth2provider.oauth2.models.Temp.AuthInformation
import com.yetu.oauth2provider.services.data.iface.{ IPermissionService, IPersonService, IAuthCodeAccessTokenService, IClientService }
import com.yetu.oauth2provider.utils.Config.SessionStatusCookie
import play.api.mvc.{ Cookie, Controller, Result }
import securesocial.core.authenticator.CookieAuthenticator
import scala.concurrent.Future
import scalaoauth2.provider
import scalaoauth2.provider._
import OAuth2Protocol._
import com.yetu.oauth2provider.oauth2.models._
import errors.InvalidState
import com.yetu.oauth2provider.utils.{ NamedLogger, Config, BearerTokenGenerator }

class AuthorizeErrorHandler(clientService: IClientService,
    personService: IPersonService,
    scopeService: ScopeService,
    permissionService: IPermissionService) extends provider.OAuth2BaseProvider with NamedLogger {

  import play.api.mvc._

  implicit def play2AuthorizeRequest(request: RequestHeader): AuthorizeRequest = {
    AuthorizeRequest(request.headers.toMap, request.queryString)
  }

  implicit def play2AuthorizeRequest[A](request: Request[A]): AuthorizeRequest = {
    val authorization = super.play2oauthRequest(request)
    AuthorizeRequest(authorization.headers, authorization.params)
  }

  def handleAuthorizeRequest[A](request: AuthorizeRequest, user: YetuUser): Either[OAuthError, AuthorizedClient] = try {

    if (request.responseType != ResponseTypes.CODE && request.responseType != ResponseTypes.TOKEN) {
      throw new InvalidGrant(s"invalid response type.")
    }
    if (request.state.length() < Config.minimumStateLength || request.state.length() > Config.maximumStateLength) {
      throw new InvalidState(s"invalid state parameter. State length is not correct.")
    }

    val client = clientService.findClient(request.clientId).getOrElse(throw new InvalidClient(s"client_id '${request.clientId}' does not exist"))

    val validScopes: List[String] = if (client.coreYetuClient) {
      client.scopes.getOrElse(List.empty)
    } else {
      scopeService.getScopeFromPermission(permissionService.findPermission(user.identityId.userId, client.clientId))
    }
    val requestScopeString = request.scope.getOrElse(Config.SCOPE_ID)

    val requestScopes: List[String] = requestScopeString.split(' ').toList

    requestScopes.foreach { requestScope =>
      if (!validScopes.contains(requestScope)) {
        throw new InvalidScope(s"invalid scope: $requestScope")
      }
    }

    val validRedirectUrls = client.redirectURIs

    //If there is no redirect url in the request then we fetch the first url from LDAP as a default one
    val redirectUrl = URLDecoder.decode(request.redirectUri.getOrElse(validRedirectUrls.head), "UTF-8")

    if (!validRedirectUrls.contains(redirectUrl)) {
      logger.warn(s"clientID:[${client.clientId}] request redirect url is NOT VALID! [$redirectUrl]. Only allowed ones are : $validRedirectUrls}")
      if (Config.redirectURICheckingEnabled) {
        throw new RedirectUriMismatch(s"invalid redirect url.")
      }
    }

    val authorizedClient = AuthorizedClient(client, request, redirectUrl)

    Right(authorizedClient)

  } catch {
    case e: OAuthError => Left(e)
  }

  def validateParameters[A](user: YetuUser)(callback: AuthorizedClient => Result)(implicit request: play.api.mvc.Request[A]): Result = {
    handleAuthorizeRequest(request, user) match {
      case Left(e) if e.statusCode == 400 => BadRequest.withHeaders(responseOAuthErrorHeader(e))
      case Left(e) if e.statusCode == 401 => Unauthorized.withHeaders(responseOAuthErrorHeader(e))
      case Right(client)                  => callback(client)
    }
  }

  def validateParametersAsync[A](user: YetuUser)(callback: AuthorizedClient => Future[Result])(implicit request: play.api.mvc.Request[A]): Future[Result] = {
    handleAuthorizeRequest(request, user) match {
      case Left(e) if e.statusCode == 400 => Future.successful(BadRequest.withHeaders(responseOAuthErrorHeader(e)))
      case Left(e) if e.statusCode == 401 => Future.successful(Unauthorized.withHeaders(responseOAuthErrorHeader(e)))
      case Right(client)                  => callback(client)
    }
  }
}

class AuthorizeService(authAccessService: IAuthCodeAccessTokenService,
    personService: IPersonService,
    scopeService: ScopeService,
    permissionService: IPermissionService) extends Controller {

  def handlePermittedApp(client: OAuth2Client, redirectUri: Option[String], state: String, scopeFromRequest: Option[String], user: YetuUser, userDefinedScopes: Option[List[String]] = None) = {

    val auth_code = BearerTokenGenerator.generateToken(Config.OAuth2.authTokenLength)
    val queryString: Map[String, Seq[String]] = Map(
      ResponseTypes.CODE -> Seq(auth_code),
      AuthorizeParameters.STATE -> Seq(state)
    )

    /*
    Get the scope the user has defined when granting permissions;
    if the user did not set any (because of core yetu app), get the scope of the request if it exists;
    fallback to default scope of a certain client;
    fallback to the most basic ID scope
     */
    val scope = scopeService.getFirstScope(userDefinedScopes).
      getOrElse(scopeFromRequest.
        getOrElse(scopeService.getFirstScope(client.scopes).
          getOrElse(Config.SCOPE_ID)))

    val redirectUrl = redirectUri.getOrElse(client.redirectURIs.head)

    authAccessService.saveAuthCode(user, auth_code)
    authAccessService.saveAuthCodeToAuthInfo(auth_code, new AuthInformation(user, Some(client.clientId), Some(scope), Some(redirectUrl)))
    Redirect(redirectUrl, queryString).withCookies(getAdditionalSessionStateCookie(user.userId))
  }

  def getAdditionalSessionStateCookie(userId: String): Cookie = {
    val fullUser: Option[YetuUser] = personService.findYetuUser(userId)
    val userUUID = fullUser.map(_.uid).getOrElse("unknownUser")

    Cookie(
      SessionStatusCookie.cookieName,
      userUUID,
      if (CookieAuthenticator.makeTransient)
        CookieAuthenticator.Transient
      else Some(CookieAuthenticator.absoluteTimeoutInSeconds),
      SessionStatusCookie.cookiePath,
      SessionStatusCookie.cookieDomain,
      secure = SessionStatusCookie.cookieSecure,
      httpOnly = SessionStatusCookie.cookieHttpOnly
    )
  }

  def handlePermittedApps(client: OAuth2Client, authorizeRequest: AuthorizeRequest, user: YetuUser, userDefinedScopes: Option[List[String]] = None): Result = {
    handlePermittedApp(client, authorizeRequest.redirectUri, authorizeRequest.state, authorizeRequest.scope, user, userDefinedScopes)
  }

  def handleClientPermissions(client: OAuth2Client, authorizeRequest: AuthorizeRequest, user: YetuUser): Result = {
    val clientPermission: Option[ClientPermission] = permissionService.findPermission(user.identityId.userId, client.clientId)
    clientPermission match {
      case None => {
        //TODO: FIXME
        Ok("TODO: permissions form should be here!")
        //Ok(com.yetu.oauth2provider.views.html.permissions(permissionsForm, client.clientName, Some(client.clientId), authorizeRequest.redirectUri, Some(authorizeRequest.state)))
      }
      case Some(permission) => handlePermittedApps(client, authorizeRequest, user, userDefinedScopes = permission.scopes)
    }
  }

}

