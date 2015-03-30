package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.Config.FrontendConfiguration
import com.yetu.oauth2provider.views.html.setup
import play.api.Logger
import play.api.mvc._
import com.yetu.oauth2provider.views
import play.filters.csrf.{ CSRFAddToken, CSRFCheck }
import securesocial.controllers.{ RegistrationInfo, BaseRegistration }
import securesocial.core.{ RuntimeEnvironment }

import scala.concurrent.Future

class SetupController(override implicit val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser] {

  val logger = Logger("com.yetu.oauth2provider.controllers.setup.setupController")

  override def startSignUp = CSRFAddToken{
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
  override def handleStartSignUp = CSRFCheck {
    Action.async {
      implicit request =>
        form.bindFromRequest.fold(
          errors => {
            Future.successful(BadRequest(com.yetu.oauth2provider.views.html.setup.startSignUpForSetup(errors)))
          },
          (registrationInfo: RegistrationInfo) => {
            handleStartSignUpSuccess(registrationInfo: RegistrationInfo)
          }
        )
    }
  }

  val download = Action {
    Ok(views.html.setup.download(FrontendConfiguration.setupDownloadUrlMac, FrontendConfiguration.setupDownloadUrlWin))
  }

  val confirmmail = Action {
    Ok(views.html.setup.confirmmail.render())
  }

  val confirmedmail = Action {
    Ok(views.html.setup.confirmedmail.render())
  }

}