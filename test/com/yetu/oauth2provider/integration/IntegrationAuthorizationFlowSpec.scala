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

      val fakeHeaders = FakeHeaders(Seq("Accept" -> Seq("text/html")))

      val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)
      status(responseAuthorization) mustEqual SEE_OTHER
      header("Location", responseAuthorization) mustEqual Some("http:///login")
    }

    "post correct credentials should redirect to authorize" in {

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

      val originalUrl = ("original-url", fullAuthorizationUrl)
      val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters, sessions = List(originalUrl))

      status(cookieResponse) mustEqual SEE_OTHER
      header("Location", cookieResponse) mustEqual Some(fullAuthorizationUrl)
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
