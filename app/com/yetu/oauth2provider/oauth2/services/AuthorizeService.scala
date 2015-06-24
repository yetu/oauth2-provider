package com.yetu.oauth2provider
package oauth2
package services

import java.net.URLDecoder

import com.yetu.oauth2provider.models.Permission
import com.yetu.oauth2provider.oauth2.OAuth2Protocol._
import com.yetu.oauth2provider.oauth2.errors.InvalidState
import com.yetu.oauth2provider.oauth2.models._
import com.yetu.oauth2provider.services.data.interface.{ IAuthCodeAccessTokenService, IClientService, IPermissionService, IPersonService }
import com.yetu.oauth2provider.utils.Config.SessionStatusCookie
import com.yetu.oauth2provider.utils.{ BearerTokenGenerator, Config, NamedLogger }
import play.api.mvc.{ Controller, Cookie, RequestHeader, Result }
import securesocial.core.RuntimeEnvironment
import securesocial.core.authenticator.CookieAuthenticator

import scala.concurrent.Future
import scalaoauth2.provider
import scalaoauth2.provider.{ AuthInfo, _ }

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
        permissionService.findPermission(user.userId, client.clientId))
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
    userDefinedScopes: Option[List[String]]) = {

    val auth_code = BearerTokenGenerator.generateToken(Config.OAuth2.authTokenLength)
    val queryString: Map[String, Seq[String]] = Map(
      ResponseTypes.CODE -> Seq(auth_code),
      AuthorizeParameters.STATE -> Seq(state)
    )

    val scope = if (userDefinedScopes.isDefined) userDefinedScopes.map(_.mkString(" ")) else scopeFromRequest

    authAccessService.saveAuthCode(
      auth_code,
      new AuthInfo[YetuUser](user, Some(client.clientId), scope, Some(redirectUri)))

    Redirect(redirectUri, queryString).withCookies(getAdditionalSessionStateCookie(user.userId))
  }

  def getAdditionalSessionStateCookie(userId: String): Cookie = {
    Cookie(
      SessionStatusCookie.cookieName,
      userId,
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

  def handlePermittedApps(client: OAuth2Client,
    authorizeRequest: AuthorizeRequest,
    user: YetuUser,
    userDefinedScopes: Option[List[String]] = None): Result = {

    handlePermittedApp(
      client,
      authorizeRequest.redirectUri,
      authorizeRequest.state,
      authorizeRequest.scope,
      user,
      userDefinedScopes)
  }

  def handleClientPermissions(request: RequestHeader,
    env: RuntimeEnvironment[YetuUser],
    client: OAuth2Client,
    authorizeRequest: AuthorizeRequest,
    user: YetuUser): Result = {

    val clientPermission: Option[ClientPermission] = permissionService.findPermission(user.userId, client.clientId)
    clientPermission match {
      case None =>

        Ok(com.yetu.oauth2provider.views.html.permissions(
          Permission.permissionsForm,
          client.clientName,
          client.clientId,
          client.scopes.getOrElse(List.empty[String]),
          authorizeRequest.redirectUri,
          Some(authorizeRequest.state))(request, env))

      case Some(permission) =>
        /*
         * TODO:
         * here we can consider the scope from the url, if the scope on the url is not included
         * in the client.scopes means that the application is trying to ask for more permissions then
         * the one that is allowed to it.. this is the incremental permission process
         */
        handlePermittedApps(client, authorizeRequest, user, userDefinedScopes = permission.scopes)
    }
  }

}

