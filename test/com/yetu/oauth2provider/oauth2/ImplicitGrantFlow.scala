package com.yetu.oauth2provider.oauth2

import com.yetu.oauth2provider.oauth2.OAuth2Protocol.ResponseTypes
import com.yetu.oauth2provider.oauth2.models.{ ClientPermission, OAuth2Client }
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import org.scalatest.MustMatchers
import play.api.libs.json._
import play.api.mvc.Result
import play.api.test.FakeHeaders
import play.api.test.Helpers._
import securesocial.core.services.SaveMode

import scala.concurrent.Future

trait ImplicitGrantFlow extends AccessTokenRetriever with MustMatchers {


  override def getAccessTokenResponseBody: JsValue = {
    implicitFlowRequest(SCOPE_BASIC)
  }

  /**
   * following OAuth2 implicit flow and making requests to get the access token:
   *
   * /authenticate/userpass login request -> save cookie
   * /oauth2/dialog request with cookie -> save access token given in the 303 redirect
   *
   */
  def implicitFlowRequest(scope: String, clientId: String = integrationTestClientId, coreYetuClient: Boolean = false, deleteSaveTestUser: Boolean = true): JsValue = {

    val (client, userPassParameters) = prepareClientAndUser(List(scope), clientId, coreYetuClient, deleteSaveTestUser)

    val cookieResponse = postRequest(loginUrlWithUserPass, userPassParameters)
    val cookie = header("Set-Cookie", cookieResponse)

    status(cookieResponse) mustEqual (SEE_OTHER)
    cookie must be ('defined)

    val fullAuthorizationUrl = s"$implicitGrantFlow?scope=${scope}&client_id=${clientId}&redirect_uri=${client.redirectURIs.head}&response_type=${ResponseTypes.TOKEN}&state=$testStateParameter"
    val fakeHeaders = FakeHeaders(Seq("Cookie" -> Seq(cookie.get)))

    val responseAuthorization = getRequest(fullAuthorizationUrl, headers = fakeHeaders)

    header("Location", responseAuthorization) must be ('defined)

    val redirectUrlWithCode = header("Location", responseAuthorization).get

    def extractUrlParam(paramName: String, line: String, baseRedirectUrl: String = client.redirectURIs.head) = {
      line.replace(s"$baseRedirectUrl#", "")
        .split("&").find(p => {
          println("Matched line " + p)
          p.startsWith(paramName)
        }).map(_.replace(paramName + "=", ""))
    }

    val accessToken = extractUrlParam("access_token", redirectUrlWithCode).get

    val expiresIn = extractUrlParam("expires_in", redirectUrlWithCode).get
    val state = extractUrlParam("state", redirectUrlWithCode).get

    expiresIn must not be 'empty
    state mustEqual testStateParameter

    log("-------------------redirectUrlWithCode :" + redirectUrlWithCode)


    log("implicit flow does not return a json body, but query parameters. converting to json object to ease")
    JsObject(Seq("access_token" -> JsString(accessToken), "expires_in" -> JsString(expiresIn)))
  }

}

object ImplicitGrantFlow extends ImplicitGrantFlow