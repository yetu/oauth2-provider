package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.oauth2.ResourceOwnerFlow

import play.api.test.Helpers._

class IntegrationResourceOwnerFlowSpec extends IntegrationBaseSpec with ResourceOwnerFlow {

  "ResourceOwnerFlow" must {

    "yield an access token upon username/password" in {

      prepareClientAndUser()

      val params = ResourceOwnerPasswordBody(
        username = testUser.email.get,
        password = testUserPassword).postParams

      val response = postRequest(accessTokenUrl, params)

      contentAsString(response) must include("access_token")
    }

    "do not yield an access token when password wrong" in {

      prepareClientAndUser()

      val params = ResourceOwnerPasswordBody(
        username = testUser.email.get,
        password = "invalidPassword").postParams

      val response = postRequest(accessTokenUrl, params)

      status(response) must be (UNAUTHORIZED)
    }

  }

}
