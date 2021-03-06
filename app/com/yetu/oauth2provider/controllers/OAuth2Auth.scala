package com.yetu.oauth2provider
package controllers

import com.yetu.oauth2provider.models.Permission.permissionsForm
import com.yetu.oauth2provider.models.Permissions
import com.yetu.oauth2provider.oauth2.handlers
import com.yetu.oauth2provider.oauth2.models.{ AuthorizedClient, ClientScopes, OAuth2Client, YetuUser }
import com.yetu.oauth2provider.oauth2.services.{ AuthorizeErrorHandler, AuthorizeService }
import com.yetu.oauth2provider.services.data.interface.{ IClientService, IPermissionService }
import com.yetu.oauth2provider.utils.Config
import play.api.mvc._
import securesocial.core.RuntimeEnvironment

import scala.concurrent.{ Await, Future }
import scala.language.postfixOps
import scalaoauth2.provider._

import scala.concurrent.duration._

/**
 * Handles the following requests:
 *
 * - /oauth2/authorize (get an authorization_code; requires user to be logged in)
 * - /oauth2/access_token (get an access_token)
 * - third-party app permissions form post request by the user
 *
 */
class OAuth2Auth(authorizationHandler: handlers.AuthorizationHandler,
  errorHandler: AuthorizeErrorHandler,
  authorizeService: AuthorizeService,
  clientService: IClientService,
  permissionService: IPermissionService,
  tokenEndpoint: TokenEndpoint)(override val env: RuntimeEnvironment[YetuUser])
    extends OAuth2ImplicitControllerHelper(tokenEndpoint: TokenEndpoint) with securesocial.core.SecureSocial[YetuUser] {

  /**
   * uses the nulab OAuth library - that means for the code grant, it performs these function calls,
   * see com.yetu.oauth2provider.services.AuthorisationHandler implementation:
   *
   * validateClient(clientId, clientSecret, grantType)
   * findAuthInfoByCode(code)
   * getStoredAccessToken(authInfo)
   * isAccessTokenExpired(token)
   * refreshAccessToken(authInfo, token)
   * createAccessToken(authInfo)
   */
  def accessToken = Action.async {
    implicit request =>
      issueAccessToken(authorizationHandler)
  }

  /**
   * The OAuth2 implicit flow.
   * Returns an access token in a redirect URL.
   * Similar to 'authorizeUser' combined with 'accessToken' methods.
   */
  def accessTokenImplicit = SecuredAction.async {

    implicit request =>

      //Add user email to the request header, as this action is secured.
      //There is a record of the user
      val newHeaders: Map[String, Seq[String]] = request.headers.toMap ++ Map("email" -> Seq(request.user.email.get))
      val headers = new Headers {
        val data = newHeaders.toSeq
      }
      val queryString: Map[String, Seq[String]] = request.queryString ++ Map("grant_type" -> Seq(Config.GRANT_TYPE_TOKEN))
      val modifiedRequest = Request(request.copy(headers = headers, queryString = queryString), request.body)

      errorHandler.validateParameters(request.user) {
        (authClient: AuthorizedClient) =>
          issueAccessTokenImplicitFlow(authorizationHandler, authClient)(modifiedRequest)
      }(modifiedRequest)

  }

  /**
   *
   * SecureSocial's "SecuredAction" makes sure the user is logged in. We then have access to the user via request.user
   * validateParameters rejects all invalid parameter combinations and gives us access to an AuthorizedClient object
   *
   */
  def authorizeUser() = SecuredAction.async {
    implicit request =>
      errorHandler.validateParameters(request.user) {
        (authClient: AuthorizedClient) =>

          val client: OAuth2Client = authClient.client
          val authorizeRequest = authClient.request

          if (client.coreYetuClient) {
            authorizeService.handlePermittedClient(client, authorizeRequest, request.user)
          } else {
            authorizeService.handleNonCoreClient(request, env, client, authorizeRequest, request.user)
          }
      }

  }

  def permissionsPost = SecuredAction.async {
    implicit request =>

      val formData: Permissions = permissionsForm.bindFromRequest.get

      clientService.findClient(formData.client_id).flatMap {
        case Some(client) =>

          val clientPermission = ClientScopes(client.clientId, Some(formData.scopes.split(' ').toList))
          permissionService.savePermission(request.user.userId, clientPermission).flatMap(_ => {

            authorizeService.handlePermittedClient(
              client,
              formData.redirect_uri,
              formData.state,
              None,
              request.user,
              clientPermission.scopes)
          })

        case None => Future.successful(
          BadRequest(s"There is a problem with clientId=[${formData.client_id}]. It does not exist in our system")
        )
      }
  }

}