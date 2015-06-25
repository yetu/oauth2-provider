package com.yetu.oauth2provider.services.data.api

import play.Play

trait APIHelper {

  val Version1 = "v1"

  val currentConfig = Play.application().configuration()

  def url(endpoint: String, version: String) = {
    currentConfig.getString("/" + version + "/api/" + endpoint)
  }

  def urlForResource(endpoint: String, version: String, resource: String) = {
    url(endpoint + "/" + resource, version)
  }

}
