package com.yetu.oauth2provider.routes

import com.yetu.oauth2provider.base.BaseRoutesSpec
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.Config
import play.api.libs.json
import play.api.libs.json.Json

import play.api.test.Helpers._

class KeySpec extends BaseRoutesSpec {

  val keyUrl = "/keys"

  val testKey = YetuPublicKey("ssh-rsa AA....")
  val invalidJson = Json parse """{ "invalid": "json" }"""

  s"$keyUrl" must {

    "not accept invalid json" in {
      val response = (keyUrl ? correctToken).post(invalidJson)
      status(response) mustEqual BAD_REQUEST
    }

    "not accept missing token" in {
      val response = (keyUrl).post(Json.toJson(testKey))
      status(response) mustEqual BAD_REQUEST
    }

    "not accept wrong token" in {
      val response = (keyUrl ? wrongToken).post(Json.toJson(testKey))
      status(response) mustEqual UNAUTHORIZED
    }

    "give a 204 if key was updated" in {

      val (authInfo, token) = generateAndSaveTestVariables(Config.SCOPE_BASIC)

      val response = (keyUrl ? token.token).post(Json.toJson(testKey))
      status(response) mustEqual NO_CONTENT
    }

    " return stored key" in {
      val (authInfo, token) = generateAndSaveTestVariables(Config.SCOPE_BASIC)

      val responseOne = (keyUrl ? token.token).post(Json.toJson(testKey))
      status(responseOne) mustEqual NO_CONTENT

      val response = (keyUrl ? token.token).get
      status(response) mustEqual OK
      contentAsString(response) must include(testKey.key)

    }

    " issue a 404 notFound if key doesn't exist." in {

      val (authInfo, token) = generateAndSaveTestVariables(Config.SCOPE_BASIC, testUser2)

      val response = (keyUrl ? token.token).get
      status(response) mustEqual NOT_FOUND

    }

  }

}
