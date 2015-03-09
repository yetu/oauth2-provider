package com.yetu.oauth2provider.controllers

import play.api.mvc._

object HealthCheck extends Controller {

  val check = Action {
    Ok(com.yetu.apphome.BuildInfo.toJson)
  }
}