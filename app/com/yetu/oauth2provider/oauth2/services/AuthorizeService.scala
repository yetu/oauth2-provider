package com.yetu.oauth2provider
package oauth2
package services

import java.net.URLDecoder

import scalaoauth2.provider.AuthInfo
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

    val client = clientService
      .findClient(request.clientId)
      .getOrElse(throw new InvalidClient(s"client_id '${request.clientId}' does not exist"))

    val validScopes: List[String] = client.scopes.getOrElse(List.empty)
    if (!client.coreYetuClient) {

      scopeService.getScopeFromPermission(
        permissionService.findPermission(user.identityId.userId, client.clientId))

    }

    request.scope.foreach { scope =>
      scope.split(' ').toList.foreach { requestScope =>
        if (!validScopes.contains(requestScope)) {
          throw new InvalidScope(s"invalid scope: $requestScope")
        }
      }
    }

    val validRedirectUrls = client.redirectURIs
    val redirectUrl = URLDecoder.decode(request.redirectUri, "UTF-8")

    if (!validRedirectUrls.contains(redirectUrl)) {

      logger.warn(s"clientID:[${client.clientId}] request redirect url is NOT VALID! " +
        s"[$redirectUrl]. Only allowed ones are : $validRedirectUrls}")

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

  def handlePermittedApp(client: OAuth2Client,
    redirectUri: String,
    state: String,
    scopeFromRequest: Option[String],
    user: YetuUser,
    userDefinedScopes: Option[List[String]] = None) = {

    val auth_code = BearerTokenGenerator.generateToken(Config.OAuth2.authTokenLength)
    val queryString: Map[String, Seq[String]] = Map(
      ResponseTypes.CODE -> Seq(auth_code),
      AuthorizeParameters.STATE -> Seq(state)
    )

    authAccessService.saveAuthCode(
      auth_code,
      new AuthInfo[YetuUser](user, Some(client.clientId), scopeFromRequest, Some(redirectUri)))

    Redirect(redirectUri, queryString).withCookies(getAdditionalSessionStateCookie(user.userId))
  }

  def getAdditionalSessionStateCookie(userId: String): Cookie = {
    val fullUser: Option[YetuUser] = personService.findYetuUser(userId)
    val userUUID = fullUser.map(_.uid).getOrElse("unknownUser")

    Cookie(
      SessionStatusCookie.cookieName,
      userUUID,
      if (CookieAuthenticator.makeTransient)
        CookieAuthenticator.Transient
      else
        Some(CookieAuthenticator.absoluteTimeoutInSeconds),
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
      case None =>
        //TODO: This should be implemented
        //Ok(com.yetu.oauth2provider.views.html.permissions(permissionsForm, client.clientName, Some(client.clientId), authorizeRequest.redirectUri, Some(authorizeRequest.state)))
        Ok("OK")
      case Some(permission) => handlePermittedApps(client, authorizeRequest, user, userDefinedScopes = permission.scopes)
    }
  }

}

