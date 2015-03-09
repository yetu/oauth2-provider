package com.yetu.oauth2provider
package controllers

import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.oauth2.services.ValidationService
import com.yetu.oauth2resource.model.ValidationResponse
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Action

import scala.concurrent.Future
import scalaoauth2.provider.AuthInfo

class OAuth2Validation(validationService: ValidationService, authorizationHandler: AuthorizationHandler) extends OAuth2Controller {

  def validate = Action.async {
    implicit request =>
      authorize(authorizationHandler) {
        authInfo =>
          Future.successful(Ok(validationService.generateJsonResponseDeprecated(authInfo)))
      }
  }
}

