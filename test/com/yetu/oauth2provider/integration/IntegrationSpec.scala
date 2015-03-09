package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import play.api.test.Helpers._
import play.api.test._

/**
 *
 * Integration test from login via /authorize via /access_token to get the access token,
 * then using that to check for various permission scopes on /info and /household endpoints
 *
 */
class IntegrationSpec extends IntegrationBaseSpec with AuthorizationCodeFlow {

  s"Integration test which starts with POSTing username and password to $loginUrlWithUserPass endpoint" must {

    s"returns relative information based on scope for homescreen as a client with ${Config.SCOPE_BASIC} scope" in {

      val accessToken = oauth2AccessTokenDance(List(SCOPE_BASIC), coreYetuClient = true)
      val infoResponse = getRequest(infoUrl + s"?access_token=${accessToken}")
      contentAsString(infoResponse) must include("userId")
      contentAsString(infoResponse) must include("firstName")
      contentAsString(infoResponse) must include("lastName")
      //As the scope is basic for homescreen, then there would not be "email" field n response
      contentAsString(infoResponse) must include ("email")
    }

    s"returns relative information based on scope for homescreen as a client with ${Config.SCOPE_CONTACT} scope" in {

      val accessToken = oauth2AccessTokenDance(List(SCOPE_CONTACT), coreYetuClient = true)
      val infoResponse = getRequest(infoUrl + s"?access_token=${accessToken}")
      log(s"${contentAsString(infoResponse)}")
      contentAsString(infoResponse) must include("userId")
      contentAsString(infoResponse) must include("firstName")
      contentAsString(infoResponse) must include("lastName")
      contentAsString(infoResponse) must include ("email")
    }

    s"returns relative information based on scope for other clients (not homescreen) as a client with ${Config.SCOPE_CONTACT} scope" in {

      val accessToken = oauth2AccessTokenDance(List(SCOPE_CONTACT), clientId = "otherClientId", coreYetuClient = false)
      val infoResponse = getRequest(infoUrl + s"?access_token=${accessToken}")
      log(s"${contentAsString(infoResponse)}")
      contentAsString(infoResponse) must include("userId")
      contentAsString(infoResponse) must include("firstName")
      contentAsString(infoResponse) must include("lastName")
      contentAsString(infoResponse) must include ("email")
    }
  }

}
