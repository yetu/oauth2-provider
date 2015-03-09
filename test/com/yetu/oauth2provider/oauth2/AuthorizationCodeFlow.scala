package com.yetu.oauth2provider.oauth2

import com.yetu.oauth2provider.oauth2.OAuth2Protocol.ResponseTypes
import com.yetu.oauth2provider.oauth2.models.{ ClientPermission, OAuth2Client }
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import org.scalatest.MustMatchers
import play.api.mvc.Result
import play.api.test.FakeHeaders
import play.api.test.Helpers._
import securesocial.core.services.SaveMode

import scala.concurrent.Future

trait AuthorizationCodeFlow extends AccessTokenRetriever with MustMatchers {

  override def getAccessToken: String = {

    oauth2AccessTokenDance(List(SCOPE_BASIC))
  }

  /**
   * following OAuth2 flow and making requests to get the access token:
   *
   * /authenticate/userpass login request -> save cookie
   * /oauth2/authorize request with cookie -> save authorization_code given in the 303 redirect
   * /oauth2/access_token request with authorization code -> get access token
   *
   */
  def oauth2AccessTokenDance(scopes: List[String], clientId: String = integrationTestClientId, coreYetuClient: Boolean = false, deleteSaveTestUser: Boolean = true): String = {

    val (client, userPassParameters) = prepareClientAndUser(scopes, clientId, coreYetuClient, deleteSaveTestUser)

    val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters)
    val cookie = header("Set-Cookie", cookieResponse)

    status(cookieResponse) mustEqual (SEE_OTHER)
    cookie must be ('defined)

    val fullAuthorizationUrl = s"$authorizationUrl?scope=${scopes.mkString(" ")}&client_id=${clientId}&redirect_uri=${client.redirectURIs.head}&response_type=${ResponseTypes.CODE}&state=$testStateParameter"
    val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get)))

    val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)
    header("Location", responseAuthorization) must be ('defined)

    val redirectUrlWithCode = header("Location", responseAuthorization).get
    val code = redirectUrlWithCode.substring(redirectUrlWithCode.indexOf("=") + 1, redirectUrlWithCode.indexOf("&"))

    val clientParameters = Map(
      "grant_type" -> Seq("authorization_code"),
      "client_id" -> Seq(client.clientId),
      "client_secret" -> Seq(client.clientSecret),
      "code" -> Seq(code),
      "redirect_uri" -> Seq(s"${client.redirectURIs.head}")
    )
    val accessTokenResponse = postRequest(accessTokenUrl, clientParameters)
    contentAsString(accessTokenResponse) must include("access_token")

    val accessToken = (contentAsJson(accessTokenResponse) \ ("access_token")).as[String]
    accessToken
  }

  /**
   *
   * preprocess takes care of:
   *
   * /authenticate/userpass login request -> save cookie
   * /oauth2/authorize request with cookie -> save authorization_code given in the 303 redirect
   *
   */
  def preProcess(clientId: String,
    clientScopes: Option[List[String]] = None,
    queryScopes: Option[List[String]] = None,
    clientRedirectUrls: List[String] = List("http://dummyRedirectUrl"),
    queryRedirectUrl: Option[String] = None,
    coreYetuClient: Boolean): Future[Result] = {

    val (client, userPassParameters) = prepareClientAndUser(clientScopes.getOrElse(List(SCOPE_BASIC)), clientId, coreYetuClient, clientRedirectUrls = clientRedirectUrls)

    val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters)
    log(s"login response status: ${status(cookieResponse)}")
    log(s"login response location: ${header("Location", cookieResponse)}")
    log(s"login response cookies: ${headers(cookieResponse)}")
    log(s"login response set-cookie: ${header("Set-Cookie", cookieResponse)}")
    val cookie = header("Set-Cookie", cookieResponse)

    //    status(cookieResponse) mustEqual (SEE_OTHER)
    //    cookie must be ('defined)

    val queryScope: String = queryScopes.getOrElse(client.scopes.get).mkString(" ")

    val fullAuthorizationUrl = s"$authorizationUrl?scope=${queryScope}" +
      s"&client_id=${client.clientId}" +
      s"&redirect_uri=${queryRedirectUrl.getOrElse(client.redirectURIs.headOption.getOrElse("undefined"))}" +
      s"&response_type=${ResponseTypes.CODE}" +
      s"&state=$testStateParameter"
    val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get)))

    val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)
    log(s"auth response status: ${status(responseAuthorization)}")
    log(s"auth response location: ${header("Location", responseAuthorization)}")
    log(s"auth response cookies: ${headers(responseAuthorization)}")
    log(s"auth response set-cookie: ${header("Set-Cookie", responseAuthorization)}")
    //    header("Location", responseAuthorization) must be ('defined)

    responseAuthorization
  }

}

object AuthorizationCodeFlow extends AuthorizationCodeFlow