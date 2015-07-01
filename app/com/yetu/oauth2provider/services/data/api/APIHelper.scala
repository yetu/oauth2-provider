package com.yetu.oauth2provider.services.data.api

import play.Play

trait APIHelper {

  val PermissionApiKey = "permission.api.url"

  val Version1 = "v1"

  lazy val currentConfig = Play.application().configuration()

  def url(endpoint: String, version: String) = {
    currentConfig.getString(PermissionApiKey) + "/" + version + "/" + endpoint
  }

  def urlForResource(endpoint: String, resource: String, version: String) = {
    url(endpoint + "/" + resource, version)
  }

}
