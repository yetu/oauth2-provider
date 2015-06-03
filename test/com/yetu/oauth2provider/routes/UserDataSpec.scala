package com.yetu.oauth2provider.routes

import com.yetu.oauth2provider.base.BaseRoutesSpec
import com.yetu.oauth2provider.utils.Config
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

class UserDataSpec extends BaseRoutesSpec {

  s"POST user data to $updateUrl  endpoint " must {
    "work and give NoContent if request is correct" in {

      val (authInfo, token) = generateAndSaveTestVariables(Config.SCOPE_CONTROLCENTER)

      val headers = FakeHeaders(Seq("Content-type" -> Seq("application/json"), "Authorization" -> Seq("OAuth " + token.token)))
      val response = postRequestWithHeaderAndJsonParameters(updateUrl, headers, jsonUpdateData)
      status(response) mustEqual 204

    }

    "give unauthorized if authorization_code is not valid" in {

      val headers = FakeHeaders(Seq("Authorization" -> Seq("OAuth " + "invalid")))

      val response = postRequestWithHeaderAndJsonParameters(updateUrl, headers, jsonUpdateData)
      status(response) mustEqual UNAUTHORIZED

    }

    "give unauthorized if authorization_code does not have the allowed scope" in {

      val (authInfo, token) = generateAndSaveTestVariables(Config.SCOPE_BASIC)

      val headers = FakeHeaders(Seq("Authorization" -> Seq("OAuth " + token.token)))
      val response = postRequestWithHeaderAndJsonParameters(updateUrl, headers, jsonUpdateData)
      status(response) mustEqual UNAUTHORIZED

    }

    "give badRequest if authorization_code is in the wrong format" in {
      val headers = FakeHeaders(Seq("Authorization" -> Seq("INVALID " + testAccessToken.token)))
      val response = postRequestWithHeaderAndJsonParameters(updateUrl, headers, jsonUpdateData)
      status(response) mustEqual BAD_REQUEST

    }

    "give badRequest if Authorize header is misspelled" in {
      val headers = FakeHeaders(Seq("AuthorizatTYPO!!" -> Seq("INVALID " + testAccessToken.token)))
      val response = postRequestWithHeaderAndJsonParameters(updateUrl, headers, jsonUpdateData)
      status(response) mustEqual BAD_REQUEST

    }

    "give badRequest if authorization_code is not given" in {
      val response = postRequestWithHeaderAndJsonParameters(updateUrl)
      status(response) mustEqual BAD_REQUEST

    }

    "give badRequest if json object is not valid" in {
      //This Json object is an invalid instance of DataUpdateRequest
      val userUpdateData = """{
                             |    "firstName": {"invalid": "invalid"},
                             |    "invalid": "Smith",
                             |    "invalid": {
                             |        "country": "Germany"
                             |    }
                             |}""".stripMargin
      val parameters = Json parse userUpdateData

      val (authInfo, token) = generateAndSaveTestVariables(Config.SCOPE_CONTROLCENTER)

      val headers = FakeHeaders(Seq("Content-type" -> Seq("application/json"), "Authorization" -> Seq("OAuth " + token.token)))
      val response = postRequestWithHeaderAndJsonParameters(updateUrl, headers, parameters)
      status(response) mustEqual BAD_REQUEST

    }
  }

}