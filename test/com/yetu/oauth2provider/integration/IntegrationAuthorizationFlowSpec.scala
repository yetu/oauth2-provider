package com.yetu.oauth2provider.integration

import java.net.URL
import java.nio.file.{ Files, Paths }

import com.plasmaconduit.jwt.JSONWebToken
import com.yetu.oauth2provider.base.DefaultTestVariables
import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import com.yetu.oauth2provider.oauth2.OAuth2Protocol.ResponseTypes
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import play.api.test.FakeHeaders
import play.api.test.Helpers._
import org.scalatest.Matchers._

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

    "redirect to authorize if post correct credentials" in {

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

    "redirect to redirect uri in case of successful core client authorization" in {

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
      val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters, fakeHeaders = FakeHeaders(), sessions = List(originalUrl))

      val cookie: Option[String] = header("Set-Cookie", cookieResponse)
      val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get)))

      val redirectUrl = new URL(redirectUris.head)
      val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)

      status(responseAuthorization) mustEqual SEE_OTHER
      header("Location", responseAuthorization).foreach(location => {

        val locationUrl = new URL(location)

        locationUrl.getProtocol.mustEqual(redirectUrl.getProtocol)
        locationUrl.getHost mustEqual redirectUrl.getHost
        locationUrl.getQuery should include ("code=")
        locationUrl.getQuery should include ("state=" + testStateParameter)
      })
    }

    "redirect with code and state parameters if the permissions had been granted for non-core client" in {

      val queryScope = List(SCOPE_BASIC)
      val redirectUris = testClient.redirectURIs

      val (client, userPassParameters) = prepareClientAndUser(
        queryScope,
        testClientId,
        coreYetuClient = false,
        clientRedirectUrls = redirectUris)

      val fullAuthorizationUrl = s"$authorizationUrl?scope=$queryScope" +
        s"&client_id=${client.clientId}" +
        s"&redirect_uri=${redirectUris.head}" +
        s"&response_type=${ResponseTypes.CODE}" +
        s"&state=$testStateParameter"

      val originalUrl = ("original-url", fullAuthorizationUrl)
      val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters, fakeHeaders = FakeHeaders(), sessions = List(originalUrl))

      val cookie: Option[String] = header("Set-Cookie", cookieResponse)
      val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get)))

      val redirectUrl = new URL(redirectUris.head)
      val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)

      status(responseAuthorization) mustEqual SEE_OTHER
      header("Location", responseAuthorization).foreach(location => {

        val locationUrl = new URL(location)

        locationUrl.getProtocol.mustEqual(redirectUrl.getProtocol)
        locationUrl.getHost mustEqual redirectUrl.getHost
        locationUrl.getQuery should include ("code=")
        locationUrl.getQuery should include ("state=" + testStateParameter)
      })
    }

    "render permission page if permissions had not been granted for non-core client" in {

      val queryScope = List(SCOPE_BASIC)
      val redirectUris = testClient.redirectURIs

      val (client, userPassParameters) = prepareClientAndUser(
        queryScope,
        testClientId,
        coreYetuClient = false,
        clientRedirectUrls = redirectUris,
        grantPermissions = false)

      val fullAuthorizationUrl = s"$authorizationUrl?scope=$queryScope" +
        s"&client_id=${client.clientId}" +
        s"&redirect_uri=${redirectUris.head}" +
        s"&response_type=${ResponseTypes.CODE}" +
        s"&state=$testStateParameter"

      val originalUrl = ("original-url", fullAuthorizationUrl)
      val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters, fakeHeaders = FakeHeaders(), sessions = List(originalUrl))

      val cookie: Option[String] = header("Set-Cookie", cookieResponse)
      val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get), "Accept" -> Seq("text/html")))

      val redirectUrl = new URL(redirectUris.head)
      val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)

      status(responseAuthorization) mustEqual OK
      contentAsString(responseAuthorization) should include ("class=\"requestedPermissions\"")
    }

    "redirect with code and state parameters if the permission were granted by the user" in {

      val queryScope = List(SCOPE_BASIC)
      val redirectUris = testClient.redirectURIs

      val (client, userPassParameters) = prepareClientAndUser(
        queryScope,
        testClientId,
        coreYetuClient = false,
        clientRedirectUrls = redirectUris,
        grantPermissions = false)

      val fullAuthorizationUrl = s"$authorizationUrl?scope=$queryScope" +
        s"&client_id=${client.clientId}" +
        s"&redirect_uri=${redirectUris.head}" +
        s"&response_type=${ResponseTypes.CODE}" +
        s"&state=$testStateParameter"

      val originalUrl = ("original-url", fullAuthorizationUrl)
      val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters, fakeHeaders = FakeHeaders(), sessions = List(originalUrl))

      val cookie: Option[String] = header("Set-Cookie", cookieResponse)
      val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get), "Accept" -> Seq("text/html")))

      val redirectUrl = new URL(redirectUris.head)
      val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)

      status(responseAuthorization) mustEqual OK

      val permissionData = Map[String, Seq[String]](
        "scopes" -> Seq(queryScope.mkString(" ")),
        "client_id" -> Seq(client.clientId),
        "redirect_uri" -> Seq(redirectUris.head),
        "state" -> Seq(testStateParameter)
      )

      val permissionPost = postRequest(permissionPostUrl, permissionData, fakeHeaders = fakeHeaders)

      status(permissionPost) mustEqual SEE_OTHER
      header("Location", permissionPost).foreach(location => {

        val locationUrl = new URL(location)

        locationUrl.getProtocol.mustEqual(redirectUrl.getProtocol)
        locationUrl.getHost mustEqual redirectUrl.getHost
        locationUrl.getQuery should include ("code=")
        locationUrl.getQuery should include ("state=" + testStateParameter)
      })
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
