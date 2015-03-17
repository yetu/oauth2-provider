package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.oauth2.models.YetuUser
import play.api.data.Forms._

import play.api.data._
import play.api.i18n.Messages
import play.filters.csrf._
import play.api.mvc.Action
import securesocial.core._
import securesocial.core.authenticator.CookieAuthenticator
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.providers.utils._
import securesocial.core.services.SaveMode
import securesocial.controllers._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{ Await, Future }

/**
 * A yetu Registration controller that uses the YetuUserProfile as the user type
 *
 * @param env the environment
 */
class Registration(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser] {

  override def handleStartSignUp = CSRFCheck {
    Action.async {
      implicit request =>
        startForm.bindFromRequest.fold(
          errors => {
            Future.successful(BadRequest(env.viewTemplates.getStartSignUpPage(errors)))
          },
          e => {
            val email = e.toLowerCase
            // check if there is already an account for this email address
            env.userService.findByEmailAndProvider(email, UsernamePasswordProvider.UsernamePassword).map {
              maybeUser =>
                maybeUser match {
                  case Some(user) =>
                    // user signed up already, send an email offering to login/recover password
                    env.mailer.sendAlreadyRegisteredEmail(user)
                  case None =>
                    createToken(email, isSignUp = true).flatMap { token =>
                      env.mailer.sendSignUpEmail(email, token.uuid)
                      env.userService.saveToken(token)
                    }
                  //here you need to save userdata too
                }
                handleStartResult().flashing(Success -> Messages("securesocial.signup.thankYouCheckEmail"), Email -> email)
            }
          }
        )
    }
  }

}