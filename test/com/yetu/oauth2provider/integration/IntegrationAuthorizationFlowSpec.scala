package com.yetu.oauth2provider.integration

import java.net.URL
import java.nio.file.{ Files, Paths }

import com.plasmaconduit.jwt.JSONWebToken
import com.yetu.oauth2provider.base.DefaultTestVariables
import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import com.yetu.oauth2provider.oauth2.OAuth2Protocol.ResponseTypes
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import play.api.mvc.Result
import play.api.test.FakeHeaders
import play.api.test.Helpers._
import org.scalatest.Matchers._

import scala.concurrent.Future

class IntegrationAuthorizationFlowSpec extends IntegrationBaseSpec with AuthorizationCodeFlow with DefaultTestVariables {

  private def prepareClientAndUserAndAuthUrl(coreYetuClient: Boolean = true,
    grantPermissions: Boolean = true) = {

    val queryScope = List(SCOPE_BASIC)
    val redirectUris = testClient.redirectURIs

    val (client, userParams) = prepareClientAndUser(
      queryScope,
      testClientId,
      coreYetuClient,
      clientRedirectUrls = redirectUris,
      deleteSaveTestUser = true,
      grantPermissions = grantPermissions)

    val authUrl = s"$authorizationUrl?scope=$queryScope" +
      s"&client_id=${client.clientId}" +
      s"&redirect_uri=${redirectUris.head}" +
      s"&response_type=${ResponseTypes.CODE}" +
      s"&state=$testStateParameter"

    (client, userParams, authUrl)
  }

  private def doAuth(authUrl: String, userParams: Map[String, Seq[String]], requestPermissions: Boolean = false) = {

    val originalUrl = ("original-url", authUrl)
    val cookieResponse = postRequest(
      loginUrlWithUserPass,
      userParams,
      fakeHeaders = FakeHeaders(),
      sessions = List(originalUrl))

    val cookie: Option[String] = header("Set-Cookie", cookieResponse)
    val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get)))

    val redirectUrl = new URL(testClient.redirectURIs.head)
    val responseAuth = getRequest(authUrl, headers = fakeHeaders)

    if (requestPermissions) {

      val permissionData = Map[String, Seq[String]](
        "scopes" -> Seq(List(SCOPE_BASIC).mkString(" ")),
        "client_id" -> Seq(testClient.clientId),
        "redirect_uri" -> Seq(testClient.redirectURIs.head),
        "state" -> Seq(testStateParameter)
      )

      val permissionPost = postRequest(permissionPostUrl, permissionData, fakeHeaders = fakeHeaders)
      (permissionPost, redirectUrl)

    } else (responseAuth, redirectUrl)
  }

  private def matchSeeOtherAndQueryParameters(request: Future[Result], redirectUrl: URL) = {

    status(request) mustEqual SEE_OTHER
    header("Location", request).foreach(location => {

      val locationUrl = new URL(location)

      locationUrl.getProtocol.mustEqual(redirectUrl.getProtocol)
      locationUrl.getHost mustEqual redirectUrl.getHost
      locationUrl.getQuery should include ("code=")
      locationUrl.getQuery should include ("state=" + testStateParameter)
    })
  }

  "Authorization Flow" must {

    "redirect to login page for authorize request if user is not logged in" in {

      val (_, _, authUrl) = prepareClientAndUserAndAuthUrl()
      val fakeHeaders = FakeHeaders(Seq("Accept" -> Seq("text/html")))

      val responseAuthorization = getRequest(authUrl, headers = fakeHeaders)
      status(responseAuthorization) mustEqual SEE_OTHER
      header("Location", responseAuthorization) mustEqual Some("http:///login")
    }

    "redirect to authorize if post correct credentials" in {

      val (_, userParams, authUrl) = prepareClientAndUserAndAuthUrl()
      val originalUrl = ("original-url", authUrl)
      val cookieResponse = postRequest(loginUrlWithUserPass, userParams, sessions = List(originalUrl))

      status(cookieResponse) mustEqual SEE_OTHER
      header("Location", cookieResponse) mustEqual Some(authUrl)
    }

    "redirect to redirect uri in case of successful core client authorization" in {

      val (_, userParams, authUrl) = prepareClientAndUserAndAuthUrl()
      val (responseAuth, redirectUrl) = doAuth(authUrl, userParams)

      matchSeeOtherAndQueryParameters(responseAuth, redirectUrl)
    }

    "redirect with code and state parameters if the permissions had been granted for non-core client" in {

      val (_, userParams, authUrl) = prepareClientAndUserAndAuthUrl(coreYetuClient = false)
      val (responseAuth, redirectUrl) = doAuth(authUrl, userParams)

      matchSeeOtherAndQueryParameters(responseAuth, redirectUrl)
    }

    "render permission page if permissions had not been granted for non-core client" in {

      val (_, userParams, authUrl) = prepareClientAndUserAndAuthUrl(coreYetuClient = false, grantPermissions = false)
      val (responseAuth, _) = doAuth(authUrl, userParams)

      status(responseAuth) mustEqual OK
      contentAsString(responseAuth) should include ("class=\"requestedPermissions\"")
    }

    "redirect with code and state parameters if the permission were granted by the user" in {

      val (_, userParams, authUrl) = prepareClientAndUserAndAuthUrl(
        coreYetuClient = false,
        grantPermissions = false)

      val (responsePermissions, redirectUrl) = doAuth(authUrl, userParams, requestPermissions = true)
      matchSeeOtherAndQueryParameters(responsePermissions, redirectUrl)
    }

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
