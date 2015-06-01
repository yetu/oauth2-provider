package com.yetu.oauth2provider
package controllers

import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.services.ValidationService
import play.api.mvc.Action

import scala.concurrent.Future

class OAuth2Validation(validationService: ValidationService, authorizationHandler: AuthorizationHandler) extends OAuth2Controller {

  import scala.concurrent.ExecutionContext.Implicits.global

  def validate = Action.async {
    implicit request =>
      authorize(authorizationHandler) {
        authInfo =>
          Future.successful(Ok(validationService.generateJsonResponseDeprecated(authInfo)))
      }
  }
}

