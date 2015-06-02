package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.base.BaseIntegrationSpec
import com.yetu.oauth2provider.oauth2.models.YetuUser
import org.scalatest.concurrent.{AsyncAssertions, ScalaFutures}
import scalaoauth2.provider.AccessToken

import org.scalatest.time.{ Millis, Seconds, Span }

class RiakHandlerTest extends BaseIntegrationSpec with ScalaFutures with AsyncAssertions {

  //with ParallelTestExecution

  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Millis))

  "RiakAuthCodeAccessTokens" must {

    "save and retrieve the token properly" in {
      whenReady(authCodeAccessTokenService.saveAccessToken(testAccessToken.token, testAccessToken)) {
        result => whenReady(authCodeAccessTokenService.findAccessToken(testAccessToken.token)) {
          retrieve => retrieve.get mustBe an[AccessToken]
        }
      }
    }

    "save and retrieve the user properly" in {
      whenReady(authCodeAccessTokenService.saveAuthCode(testUser, testAuthCode)) {
        result => whenReady(authCodeAccessTokenService.findUserByAuthCode(testAuthCode)) {
          retrieve => retrieve.get mustBe an[YetuUser]
        }
      }
    }
  }

}
