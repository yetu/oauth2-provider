package com.yetu.oauth2provider.controllers

import controllers.Assets
import play.api.mvc._
import com.yetu.oauth2provider.utils.Config.OAuth2.jsonWebTokenPublicKeyFilename

object Certificates extends Controller {

  val certificates = {
    Assets.at("/public/keys", jsonWebTokenPublicKeyFilename, false)
  }
}
