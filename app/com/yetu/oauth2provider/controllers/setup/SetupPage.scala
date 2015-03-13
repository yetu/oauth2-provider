package com.yetu.oauth2provider.controllers.setup

import play.api.mvc._
import com.yetu.oauth2provider.views

object SetupPage extends Controller {

  val download = Action {
    Ok(views.html.download.render())
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