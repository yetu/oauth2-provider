package com.yetu.oauth2provider
package controllers

import com.yetu.oauth2provider.models.{ DataListWrapper, DataUpdateRequest }
import com.yetu.oauth2provider.models.HouseholdModel.householdFormat
import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.services.ScopeService
import com.yetu.oauth2provider.services.data.interface.{ IPersonService, IPublicKeyService }
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.Config
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{ Action, Result }

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class OAuth2ResourceServer(scopeService: ScopeService,
    personService: IPersonService,
    authorizationHandler: AuthorizationHandler,
    keyService: IPublicKeyService) extends OAuth2Controller {

  def info = Action.async {
    //TODO: use async interface properly already in the service class instead of a Future.successful()
    implicit request =>

      Logger.debug(s"request = $request \n \t body = ${request.body}")
      authorize(authorizationHandler) {
        authInfo =>
          Logger.debug(s"request has been authorized... = $request \n \t body = ${request.body}")

          // current user is defined via Nulab's library and AuthorizationHandler
          val user = authInfo.user

          Logger.debug(s"request has been authorized...user = $user")
          Logger.debug(s"request has been authorized...scope = ${authInfo.scope}")
          Logger.debug(s"request has been authorized...authInfo = $authInfo")

          val outputJson: Option[JsValue] = scopeService.getInfoByScope(authInfo.user, authInfo.scope.getOrElse(Config.SCOPE_ID)).map(user => Json.toJson(user))

          Logger.debug(s"request has been authorized...outputJson = $outputJson")
          outputJson match {
            case None           => Future.successful(BadRequest("You have been authorized, but your scope is invalid or not authorized, you cannot access any data for this user."))
            case Some(userJson) => Future.successful(Ok(userJson))
          }
      }
  }

  def updateUserProfile = Action.async(parse.json[DataUpdateRequest]) {
    //TODO: use async interface properly already in the service class instead of a Future.successful()
    implicit request =>
      authorize(authorizationHandler) {
        authInfo =>
          {
            val result: Option[Result] = for {
              scope: String <- authInfo.scope if scope == Config.SCOPE_CONTROLCENTER
            } yield personService.updateUserProfile(authInfo.user, request.body)

            result.fold(Future.successful(Unauthorized(s"This access token is valid but it does not have the authorized scope to update user contact and personal information."))
            )(resultOfUpdate => Future.successful(resultOfUpdate))
          }
      }
  }

  def updateKey = Action.async(parse.json[YetuPublicKey]) {
    implicit request =>
      authorize(authorizationHandler) {
        authInfo =>
          {
            keyService.storeKeyF(authInfo.user.identityId.userId, request.body)
              .map(_ => NoContent)
          }
      }
  }

  def viewKeys = Action.async { implicit request =>
    authorize(authorizationHandler) {
      authInfo =>
        {
          keyService.getKeyF(authInfo.user.identityId.userId).map {
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
