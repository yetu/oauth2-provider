package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.utils.Config.FrontendConfiguration
import play.api.mvc._
import com.yetu.oauth2provider.views

object SetupPage extends Controller {

  val download = Action {
    Ok(views.html.setup.download(FrontendConfiguration.setupDownloadUrlMac, FrontendConfiguration.setupDownloadUrlWin))
  }

  val registration = Action {
    Ok(views.html.setup.registration.render())
  }

  val confirmmail = Action {
    Ok(views.html.setup.confirmmail.render())
  }

  val confirmedmail = Action {
    Ok(views.html.setup.confirmedmail.render())
  }

}