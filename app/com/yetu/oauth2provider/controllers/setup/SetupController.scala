package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.controllers.setup.SetupController._
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.Config.FrontendConfiguration
import com.yetu.oauth2provider.views
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._
import play.filters.csrf.{ CSRFAddToken, CSRFCheck }
import securesocial.controllers.{ BaseRegistration, RegistrationInfo }
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Future

class SetupController(override implicit val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser] {

  val logger = Logger("com.yetu.oauth2provider.controllers.setup.setupController")

  override def startSignUp = CSRFAddToken {
    Action {
      implicit request => Ok(views.html.setup.startSignUpForSetup(form))
    }
  }

  //TODO: change post url (check)
  //TODO: redirecting url on success
  //TODO: redirecting url on failure (check)
  //TODO: save check fields for newsletter and agreement in database
  //TODO: add radio buttons on template form
  //TODO: decide on redirecting depending on radio button value and pass to secure social
  //
  override def handleStartSignUp = CSRFCheck {
    Action.async {
      implicit request =>
        newUserForm.bindFromRequest.fold(
          formWithErrors => {
            Future.successful(BadRequest("You need to choose whether you wish to newly register or whether you have already registered."))
          }, userRegistration => {
            userRegistration match {
              case UserNotRegistered => {
                handleNewRegistration
              }
              case UserAlreadyRegistered => {
                Future.successful(Redirect(com.yetu.oauth2provider.controllers.setup.routes.SetupController.download))
              }
              case _ =>
                Future.successful(BadRequest(
                  s"Form field $UserRegistrationStatus must be " +
                    s"one of ($UserNotRegistered, $UserAlreadyRegistered)"))
            }
          }
        )

    }
  }

  def handleNewRegistration(implicit request: Request[AnyContent]): Future[Result] = {
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

  def download = Action {
    Ok(views.html.setup.download(FrontendConfiguration.setupDownloadUrlMac, FrontendConfiguration.setupDownloadUrlWin))
  }

  def confirmmail = Action {
    Ok(views.html.setup.confirmmail.render())
  }

  def confirmedmail = Action {
    Ok(views.html.setup.confirmedmail.render())
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

