package com.yetu.oauth2provider
package integration

import com.yetu.oauth2provider.oauth2.SignatureFlow
import play.api.test.FakeHeaders
import play.api.test.Helpers._

import com.yetu.oauth2provider.utils.DateUtility

class IntegrationSignedHttpSpec extends IntegrationBaseSpec with SignatureFlow {

  def invalidHeaders = signedHeaders(FakeHeaders(Seq(
    "date" -> Seq(DateUtility.rfcFormatToStringWithUTC(calendar.getTime)),
    "email" -> Seq("invalid@test.test")
  )), List("date", "email"))

  s"$loginUrlWithSignedHttp" ignore {

    "return BAD_REQUEST if something with the request is wrong" in {
      val response = postRequestWithHeaderAndJsonParameters(loginUrlWithSignedHttp)
      status(response) mustEqual BAD_REQUEST
    }

    "return UNAUTHORIZED if request was correctly signed but email is not matching" in {
      setupUser()

      val response = postRequestWithHeaderAndJsonParameters(loginUrlWithSignedHttp, headers = invalidHeaders)
      status(response) mustEqual UNAUTHORIZED
    }

    "return SEE_OTHER(REDIRECTING) if request was correctly signed" in {
      setupUser()

      val response = postRequestWithHeaderAndJsonParameters(loginUrlWithSignedHttp, headers = validHeaders)
      status(response) mustEqual SEE_OTHER
    }

    " give a valid id= cookie if request was correctly signed and user exists" in {
      setupUser()

      val response = postRequestWithHeaderAndJsonParameters(loginUrlWithSignedHttp, headers = validHeaders)
      val cookie = header("Set-Cookie", response)
      cookie must be('defined)
    }

  }

  def invalidParams: Map[String, Seq[String]] = {
    Map(
      "client_secret" -> Seq(integrationTestSecret),
      "client_id" -> Seq(integrationTestClientId),
      "grant_type" -> Seq("other_grant_type")
    )
  }

  s"$accessTokenUrl " must {

    "reject signature request if headers are invalid  " in {

      prepareClientAndUser()
      setupKey()

      val response = postRequest(accessTokenUrl, validParams, invalidHeaders)
      status(response) mustEqual UNAUTHORIZED

    }

    "reject signature request if it has something else than grant_type=signature in the post body" in {

      prepareClientAndUser()
      setupKey()

      val response = postRequest(accessTokenUrl, invalidParams, validHeaders)
      status(response) mustEqual BAD_REQUEST

    }

    "support a signature grant type and give an access_token when all is good " in {

      prepareClientAndUser()
      setupKey()

      val response = postRequest(accessTokenUrl, validParams, validHeaders)
      status(response) mustEqual OK
      contentAsString(response) must include("access_token")
    }

  }

}
