package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.utils.Config.FrontendConfiguration
import play.api.mvc._
import com.yetu.oauth2provider.views

object SetupPage extends Controller {

  val download = Action {
    Ok(views.html.download(FrontendConfiguration.setupDownloadUrlMac, FrontendConfiguration.setupDownloadUrlWin))
  }

  val registration = Action {
    Ok(views.html.registration.render())
  }

  val confirmmail = Action {
    Ok(views.html.confirmmail.render())
  }

  val confirmedmail = Action {
    Ok(views.html.confirmedmail.render())
  }

}