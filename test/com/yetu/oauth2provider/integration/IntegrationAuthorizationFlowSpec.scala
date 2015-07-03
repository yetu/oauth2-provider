package com.yetu.oauth2provider.integration

import java.nio.file.{ Files, Paths }

import com.plasmaconduit.jwt.JSONWebToken
import com.yetu.oauth2provider.base.DefaultTestVariables
import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import com.yetu.oauth2provider.oauth2.OAuth2Protocol.ResponseTypes
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import play.api.test.FakeHeaders
import play.api.test.Helpers._

class IntegrationAuthorizationFlowSpec extends IntegrationBaseSpec with AuthorizationCodeFlow with DefaultTestVariables {

  "Authorization Flow" must {

    "redirect to login page for authorize request if user is not logged in" in {

      val queryScope = List(SCOPE_BASIC)
      val redirectUris = testClient.redirectURIs

      val (client, userPassParameters) = prepareClientAndUser(
        queryScope,
        testClientId,
        coreYetuClient = true,
        clientRedirectUrls = redirectUris)

      val fullAuthorizationUrl = s"$authorizationUrl?scope=$queryScope" +
        s"&client_id=${client.clientId}" +
        s"&redirect_uri=${redirectUris.head}" +
        s"&response_type=${ResponseTypes.CODE}" +
        s"&state=$testStateParameter"

      val fakeCookie = "id=7c41c131334bcdc33c04cc0002205dfede54556de850070d06b52c135a756beb22a2a57ea69dc12e06b980bb75ee1372d044360474dc88bb0c9e6712b984998c595b8b4c1937e20ffe15e2f1ab154ddf11055a35c5c4fd4ba96344ee25aee89d38eb45a25b058cda5674e161acbab27b713744d9e435313e092f78171ea29fa6;"
      val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(fakeCookie)))

      val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)
      status(responseAuthorization) mustEqual SEE_OTHER
      header("Location", responseAuthorization) mustEqual loginUrlWithUserPass
    }

  }

  "IntegrationAuthorizationFlow" ignore {

    "yield a response authorization Result" ignore {
      registerClientAndUserAndAuthenticate(integrationTestClientId, clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(s"$defaultRedirectUrl Invalid"), coreYetuClient = true)
    }
  }

  "OAuth2 flows " ignore {

    oauth2flowImplementations.foreach { implementation =>

      s"support yielding an access_token for the ${implementation.implementationId} flow" in {

        val accessToken = implementation.getAccessToken
        accessToken.length must be > 10
      }

      s"support yielding an access_token which is a JWT and contains expected fields userUUID and clientId for the ${implementation.implementationId} flow" in {

        val accessToken = implementation.getAccessToken

        val publicKey: Array[Byte] = Files.readAllBytes(Paths.get(Config.OAuth2.jsonWebTokenPublicKeyFilename))
        val value = JSONWebToken.verify(publicKey, accessToken).get // throws exception if verification fails

        log(s"contents of JWT token:: $value")

        value.toString must include ("userUUID")
        value.toString must include ("clientId")

      }

    }
  }

}
