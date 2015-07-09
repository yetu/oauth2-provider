package com.yetu.oauth2provider.oauth2

import com.yetu.oauth2provider.utils.Config._
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

  override def getAccessToken() = {

    prepareClientAndUser()
    val params = ResourceOwnerPasswordBody(
      username = testUser.email.get,
      password = testUserPassword).postParams

    val response = postRequest(accessTokenUrl, params)

    val accessToken = (contentAsJson(response) \ ("access_token")).as[String]
    accessToken

  }

}

object ResourceOwnerFlow extends ResourceOwnerFlow
