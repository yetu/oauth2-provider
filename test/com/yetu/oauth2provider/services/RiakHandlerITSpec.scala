package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.base.BaseIntegrationSpec
import com.yetu.oauth2provider.oauth2.models.YetuUser
import org.scalatest.concurrent.{ AsyncAssertions, ScalaFutures }
import scala.concurrent.Future
import scalaoauth2.provider.{ AuthInfo, AccessToken }

import scala.concurrent.ExecutionContext.Implicits.global

import org.scalatest.time.{ Millis, Seconds, Span }

class RiakHandlerITSpec extends BaseIntegrationSpec with ScalaFutures with AsyncAssertions {

  //with ParallelTestExecution

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

  "Riak MailToken Handler" must {
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
