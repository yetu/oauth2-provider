package com.yetu.oauth2provider.integration

import play.api.test.Helpers._

class IntegrationValidationSpec extends IntegrationBaseSpec {

  "validation of access token generated by different flows " must {

    oauth2flowImplementations.foreach { implementation =>

      s"yield a UUID and email for the ${implementation.implementationId} flow" in {

        val accessToken = implementation.getAccessToken

        val response = (validateUrl ? accessToken).get

        log(s"validationJson: ${contentAsString(response)}")
        status(response) mustEqual OK
        contentAsString(response) must include(testUserInfo.user.email)
      }
    }
  }
}
