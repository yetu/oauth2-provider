package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.base.BaseIntegrationSpec
import org.scalatest.concurrent.{ AsyncAssertions, ScalaFutures }
import org.scalatest.time.{ Millis, Seconds, Span }

import scala.concurrent.ExecutionContext.Implicits.global

class RiakHandlerITSpec extends BaseIntegrationSpec with ScalaFutures with AsyncAssertions {

  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Millis))

  "RiakAuthCodeAccessTokens" must {

    "save and retrieve the token" in {

      whenReady(authCodeAccessTokenService.saveAccessToken(testAccessToken.token, testAccessToken)) {
        result =>
          whenReady(authCodeAccessTokenService.findAccessToken(testAccessToken.token)) {
            retrieve => retrieve.get mustBe testAccessToken
          }
      }
    }

    "save and retrieve the authInfo by authorization_code" in {

      val resultFuture = for {
        save <- authCodeAccessTokenService.saveAuthCode(testAuthCode, testUserInfo)
        retrieve <- authCodeAccessTokenService.findAuthInfoByAuthCode(testAuthCode)
      } yield retrieve

      whenReady(resultFuture) {
        result => result.get mustBe testUserInfo
      }

    }

    "save and retrieve the authInfo by access_token" in {

      val resultFuture = for {
        save <- authCodeAccessTokenService.saveAccessTokenToAuthInfo(testAccessToken.token, testUserInfo)
        retrieve <- authCodeAccessTokenService.findAuthInfoByAccessToken(testAccessToken.token)
      } yield retrieve

      whenReady(resultFuture) {
        result => result.get mustBe testUserInfo
      }

    }

    "save and retrieve the accessToken by authInfo" in {

      val key = testUserInfo.clientId.get + testUserInfo.user.userId

      val resultFuture = for {
        save <- authCodeAccessTokenService.saveAuthInfoToAccessToken(key, testAccessToken)
        retrieve <- authCodeAccessTokenService.findAccessTokenByAuthInfo(key)
      } yield retrieve

      whenReady(resultFuture) {
        result => result.get mustBe testAccessToken
      }

    }

    "no conflict for access tokens" in {

      val resultFuture = for {
        save <- authCodeAccessTokenService.saveAccessToken(testAccessToken.token, testAccessToken)
        save <- authCodeAccessTokenService.saveAccessTokenToAuthInfo(testAccessToken.token, testUserInfo)
        retrieveAuthInfo <- authCodeAccessTokenService.findAuthInfoByAccessToken(testAccessToken.token)
        retrieveAccessToken <- authCodeAccessTokenService.findAccessToken(testAccessToken.token)
      } yield (retrieveAccessToken, retrieveAuthInfo)

      whenReady(resultFuture) {
        result =>
          {
            result._1.get mustBe testAccessToken
            result._2.get mustBe testUserInfo
          }
      }

    }
  }

  "RiakMailToken" must {
    "save and retrieve the mailtoken" in {

      val resultFuture = for {
        save <- mailTokenService.saveToken(testMailToken)
        retrieve <- mailTokenService.findToken(testMailToken.uuid)
      } yield retrieve

      whenReady(resultFuture) {
        result => result.get mustBe testMailToken
      }
    }
  }

}
