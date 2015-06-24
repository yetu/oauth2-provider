package com.yetu.oauth2provider.oauth2

import com.yetu.oauth2provider.utils.Config._
import play.api.libs.json.JsValue
import play.api.test.Helpers._

trait ResourceOwnerFlow extends AccessTokenRetriever {

  case class ResourceOwnerPasswordBody(username: String,
      password: String,
      clientId: String = integrationTestClientId,
      clientSecret: String = integrationTestSecret,
      grantType: String = GRANT_TYPE_RESOURCE_OWNER_PASSWORD) {
    def postParams = Map(
      "username" -> Seq(username),
      "password" -> Seq(password),
      "client_id" -> Seq(clientId),
      "client_secret" -> Seq(clientSecret),
      "grant_type" -> Seq(grantType)
    )
  }

  override def getAccessTokenResponseBody = {

    prepareClientAndUser()
    val params = ResourceOwnerPasswordBody(
      username = testUser.userId,
      password = testUserPassword).postParams

    val response = postRequest(accessTokenUrl, params)

    val x: JsValue = contentAsJson(response)

    contentAsJson(response)

  }

}

object ResourceOwnerFlow extends ResourceOwnerFlow
