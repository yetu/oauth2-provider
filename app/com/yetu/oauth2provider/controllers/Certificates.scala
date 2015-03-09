package com.yetu.oauth2provider.controllers

import controllers.Assets
import play.api.mvc._

object Certificates extends Controller {

  val certificates = {
    Assets.at("/public/keys", "public_key.der", false);
  }
}
