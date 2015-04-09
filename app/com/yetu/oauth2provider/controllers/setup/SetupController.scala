package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.controllers.setup.SetupController._
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.Config.FrontendConfiguration
import com.yetu.oauth2provider.views
import play.api.Logger
import play.api.data.Form
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.mvc._
import play.filters.csrf.{ CSRFAddToken, CSRFCheck }
import securesocial.controllers.BaseRegistration._
import securesocial.controllers.{ BaseRegistration, RegistrationInfo }
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.UsernamePasswordProvider

import scala.concurrent.Future

class SetupController(override implicit val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser] {

  val logger = Logger("com.yetu.oauth2provider.controllers.setup.setupController")

  override def startSignUp = CSRFAddToken {
    Action {
      implicit request => Ok(views.html.setup.startSignUpForSetup(form))
    }
  }

  //TODO: redirecting url on success
  //TODO: save check fields for newsletter and agreement in database
  //
  override def handleStartSignUp = CSRFCheck {
    Action.async {
      implicit request =>
        newUserForm.bindFromRequest.fold(
          formWithErrors => { invalidRadioButtonChoice(Some(Json.prettyPrint(formWithErrors.errorsAsJson))) },
          userRegistration => {
            userRegistration match {
              case UserNotRegistered => {
                handleNewRegistration
              }
              case UserAlreadyRegistered => {
                Future.successful(Redirect(com.yetu.oauth2provider.controllers.setup.routes.SetupController.download))
              }
              case _ => invalidRadioButtonChoice()
            }
          }
        )

    }
  }

  private def handleNewRegistration(implicit request: Request[AnyContent]): Future[Result] = {
    form.bindFromRequest.fold(
      (errors: Form[RegistrationInfo]) => {
        logger.warn(s"""user (email=${errors.data.get("email")}) started sign-up process
          but failed to fill fields correctly: ${Json.prettyPrint(errors.errorsAsJson)})
           """.stripMargin)
        Future.successful(BadRequest(com.yetu.oauth2provider.views.html.setup.startSignUpForSetup(errors)))
      },
      (registrationInfo: RegistrationInfo) => {
        logger.info(s"New user started sign-up process with email: ${registrationInfo.email}")
        handleStartSignUpSuccess(registrationInfo)
      }
    )
  }

  override def handleStartSignUpSuccess(registrationInfo: RegistrationInfo)(implicit request: Request[AnyContent]) = {
    val email = registrationInfo.email.toLowerCase
    // check if there is already an account for this email address
    env.userService.findByEmailAndProvider(email, UsernamePasswordProvider.UsernamePassword).map {
      maybeUser =>
        maybeUser match {
          case Some(user) =>
            // user signed up already, send an email offering to login/recover password
            env.mailer.sendAlreadyRegisteredEmail(user)
          case None =>
            createToken(registrationInfo, isSignUp = true).flatMap { token =>
              val savedToken = env.userService.saveToken(token)
              env.mailer.sendSignUpEmail(email, token.uuid)
              savedToken
            }
        }
        Redirect(com.yetu.oauth2provider.controllers.setup.routes.SetupController.confirmmail())
    }
  }

  def download = Action {
    Ok(views.html.setup.download(FrontendConfiguration.setupDownloadUrlMac, FrontendConfiguration.setupDownloadUrlWin))
  }

  def confirmmail = Action {
    Ok(views.html.setup.confirmmail.render())
  }

  def confirmedmail = Action {
    Ok(views.html.setup.confirmedmail.render())
  }

  private def invalidRadioButtonChoice(error: Option[String] = None) = {
    Future.successful(BadRequest(
      s"Form field $UserRegistrationStatus must be " +
        s"one of ($UserNotRegistered, $UserAlreadyRegistered)"
        + s"AdditionalInformation: ${error}"))
  }

}

object SetupController {

  import play.api.data.Forms._
  import play.api.data._

  val UserRegistrationStatus = "UserRegistrationStatus"
  val UserNotRegistered = "UserNotRegistered"
  val UserAlreadyRegistered = "UserAlreadyRegistered"

  val newUserForm: Form[String] = Form(
    single(
      UserRegistrationStatus -> nonEmptyText
    )
  )

  val filledNewUserForm = newUserForm.fill(UserNotRegistered)
}

