package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.Config.FrontendConfiguration
import com.yetu.oauth2provider.views.html.setup
import play.api.mvc._
import com.yetu.oauth2provider.views
import securesocial.controllers.{ BaseRegistration }
import securesocial.core.{ RuntimeEnvironment }

class SetupController(override implicit val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser] {

  override def startSignUp = {
    Action {
      implicit request => Ok(views.html.setup.startSignUpForSetup(form))
    }
  }

  override def handleStartSignUp = {
    super.handleStartSignUp
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