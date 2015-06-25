package com.yetu.oauth2provider
package controllers

import com.yetu.oauth2provider.models.HouseholdModel.householdFormat
import com.yetu.oauth2provider.models.{ DataListWrapper, DataUpdateRequest }
import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.services.ScopeService
import com.yetu.oauth2provider.services.data.interface.{ IPersonService, IPublicKeyService }
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.Config
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.Action

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OAuth2ResourceServer(scopeService: ScopeService,
    personService: IPersonService,
    authorizationHandler: AuthorizationHandler,
    keyService: IPublicKeyService) extends OAuth2Controller {

  def info = Action.async {
    implicit request =>
      authorize(authorizationHandler) { authInfo =>

        val user = authInfo.user

        Logger.debug(s"request has been authorized...user = $user")
        Logger.debug(s"request has been authorized...scope = ${authInfo.scope}")
        Logger.debug(s"request has been authorized...authInfo = $authInfo")

        val outputJson: Option[JsValue] = scopeService.getInfoByScope(
          authInfo.user,
          authInfo.scope.getOrElse(Config.SCOPE_ID)).map(user => Json.toJson(user))

        val result = outputJson match {
          case Some(userJson) => Ok(userJson)
          case None => BadRequest("You have been authorized, but your scope is invalid " +
            "or not authorized, you cannot access any data for this user.")
        }

        Future.successful(result)
      }
  }

  def updateUserProfile() = Action.async(parse.json[DataUpdateRequest]) {
    implicit request =>
      authorize(authorizationHandler) {
        authInfo =>
          {

            if (authInfo.scope.getOrElse("").equals(Config.SCOPE_CONTROLCENTER)) {

              personService
                .updateUserProfile(authInfo.user, request.body)
                .map(_ => NoContent)

            } else {
              Future.successful(Unauthorized(s"This access token is valid but it does not " +
                s"have the authorized scope to update user contact and personal information."))
            }
          }
      }
  }

  def updateKey() = Action.async(parse.json[YetuPublicKey]) {
    implicit request =>
      authorize(authorizationHandler) {
        authInfo =>
          {
            keyService.storeKeyF(authInfo.user.userId, request.body)
              .map(_ => NoContent)
          }
      }
  }

  def viewKeys = Action.async { implicit request =>
    authorize(authorizationHandler) {
      authInfo =>
        {
          keyService.getKeyF(authInfo.user.userId).map {
            case Some(key) => {
              val result = DataListWrapper(List(key))
              Ok(Json.toJson(result))
            }
            case None => NotFound
          }
        }
    }
  }

  def index = Action {
    TemporaryRedirect(Config.redirectAfterLogin)
  }

}
