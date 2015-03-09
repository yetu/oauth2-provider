package com.yetu.oauth2provider
package routes

import com.yetu.oauth2provider.base.BaseRoutesSpec
import com.yetu.oauth2provider.utils.Config
import play.api.mvc.AnyContentAsEmpty

import play.api.test._
import play.api.test.Helpers._

class OAuth2ValidationSpec extends BaseRoutesSpec {

  s"validation on $validateUrl" must {

    s"give badrequest if no token" in {
      val Some(result) = route(FakeRequest(GET, validateUrl))
      status(result) mustEqual (BAD_REQUEST)
    }

    s"give unauthorized if not valid" in {

      val Some(result) = route(FakeRequest(GET, s"$validateUrl?access_token=dummyAccessToken"))
      status(result) mustEqual (UNAUTHORIZED)
    }

    s"give a correct 200 response if token is valid either in the header or in the query parameter" in {

      val (testUserInfo, testAccessToken) = generateTestVariables()
      authCodeAccessTokenService.saveAccessToken(testAccessToken.token, testAccessToken)
      authCodeAccessTokenService.saveAccessTokenToUser(testAccessToken, testUserInfo)

      // use /validate/?access_token=<token>
      val Some(result) = route(FakeRequest(GET, s"$validateUrl?access_token=${testAccessToken.token}"))
      status(result) mustEqual OK

      // use [Authorization: OAuth <token>]  header
      val headers = FakeHeaders(Seq("Content-type" -> Seq("application/json"), "Authorization" -> Seq("OAuth " + testAccessToken.token)))
      val Some(result2) = route(FakeRequest(GET, validateUrl, headers, AnyContentAsEmpty))

      status(result2) mustEqual OK

    }

    s"give proper json if all correct" in {
      val (testUserInfo, testAccessToken) = generateTestVariables(scope = Config.SCOPE_CONTROLCENTER)
      authCodeAccessTokenService.saveAccessToken(testAccessToken.token, testAccessToken)
      authCodeAccessTokenService.saveAccessTokenToUser(testAccessToken, testUserInfo)

      val Some(result) = route(FakeRequest(GET, s"$validateUrl?access_token=${testAccessToken.token}"))

      status(result) mustEqual OK
      contentType(result) mustEqual Some("application/json")
      charset(result) mustEqual Some("utf-8")
      contentAsString(result) must include ("scope")
      contentAsString(result) must include (testAccessToken.scope.get)
      contentAsString(result) must include (testUserInfo.user.uid)
      logger.info(s"validationJson: ${contentAsString(result)}")
    }

  }

}
