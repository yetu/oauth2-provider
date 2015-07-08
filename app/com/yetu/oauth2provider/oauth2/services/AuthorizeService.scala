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

import scala.concurrent.ExecutionContext.Implicits.global

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

  def handleAuthorizeRequest[A](request: AuthorizeRequest, user: YetuUser): Future[Either[OAuthError, AuthorizedClient]] = try {

    if (request.responseType != ResponseTypes.CODE && request.responseType != ResponseTypes.TOKEN) {
      throw new InvalidGrant(s"invalid response type.")
    }
    if (request.state.length() < Config.minimumStateLength || request.state.length() > Config.maximumStateLength) {
      throw new InvalidState(s"invalid state parameter. State length is not correct.")
    }

    //TODO: validate scopes on the request, need to create the scopes service that query for all at permission API

    clientService
      .findClient(request.clientId)
      .map(c => try {

        val client = c.getOrElse(throw new InvalidClient(s"client_id '${request.clientId}' does not exist"))
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
      })

  } catch {
    case e: OAuthError => Future.successful(Left(e))
  }

  def validateParameters[A](user: YetuUser)(callback: AuthorizedClient => Future[Result])(implicit request: play.api.mvc.Request[A]): Future[Result] = {
    handleAuthorizeRequest(request, user).flatMap {
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

  def handlePermittedClient(client: OAuth2Client,
    redirectUri: String,
    state: String,
    scopeFromRequest: Option[String],
    user: YetuUser,
    userDefinedScopes: Option[List[String]]): Future[Result] = {

    val auth_code = BearerTokenGenerator.generateToken(Config.OAuth2.authTokenLength)
    val queryString: Map[String, Seq[String]] = Map(
      ResponseTypes.CODE -> Seq(auth_code),
      AuthorizeParameters.STATE -> Seq(state)
    )

    val scope = if (userDefinedScopes.isDefined) {
      userDefinedScopes.map(_.mkString(" "))
    } else scopeFromRequest

    val authInfo = new AuthInfo[YetuUser](user, Some(client.clientId), scope, Some(redirectUri))

    authAccessService.saveAuthCode(auth_code, authInfo).map(_ =>
      Redirect(redirectUri, queryString).withCookies(getAdditionalSessionStateCookie(user.userId))
    )
  }

  def handlePermittedClient(client: OAuth2Client,
    authorizeRequest: AuthorizeRequest,
    user: YetuUser,
    userDefinedScopes: Option[List[String]] = None): Future[Result] = {

    handlePermittedClient(
      client,
      authorizeRequest.redirectUri,
      authorizeRequest.state,
      authorizeRequest.scopes,
      user,
      userDefinedScopes)
  }

  def handleNonCoreClient(request: RequestHeader,
    env: RuntimeEnvironment[YetuUser],
    client: OAuth2Client,
    authorizeRequest: AuthorizeRequest,
    user: YetuUser): Future[Result] = {

    permissionService.findPermission(user.userId, client.clientId).flatMap {
      case None =>

        val renderPermissions = Ok(com.yetu.oauth2provider.views.html.permissions(
          Permission.permissionsForm,
          client.clientName,
          client.clientId,
          authorizeRequest.scopes.getOrElse(""),
          authorizeRequest.redirectUri,
          Some(authorizeRequest.state))(request, env))

        Future.successful(renderPermissions)

      case Some(permission) =>
        /*
         * TODO:
         * here we can consider the scope from the url, if the scope on the url is not included
         * in the client.scopes means that the application is trying to ask for more permissions then
         * the one that is allowed to it.. this is the incremental permission process
         */
        handlePermittedClient(client, authorizeRequest, user, userDefinedScopes = permission.scopes)
    }
  }

}

