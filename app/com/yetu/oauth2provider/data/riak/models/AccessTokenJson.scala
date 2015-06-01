package com.yetu.oauth2provider.data.riak.models

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scalaoauth2.provider.AccessToken

object AccessTokenJson {
  implicit val jsonFormat: RootJsonFormat[AccessToken] = jsonFormat5(AccessToken.apply)
}
