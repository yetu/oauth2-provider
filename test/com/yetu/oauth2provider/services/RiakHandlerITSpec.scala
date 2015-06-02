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

      val resultFuture: Future[Option[AuthInfo[YetuUser]]] = for {
        save <- authCodeAccessTokenService.saveAuthCode(testAuthCode, testUserInfo)
        retrieve <- authCodeAccessTokenService.findAuthInfoByAuthCode(testAuthCode)
      } yield retrieve

      whenReady(resultFuture) {
        result => result.get mustBe testUserInfo
      }

    }

    "save and retrieve the authInfo by access_token" in {

      val resultFuture: Future[Option[AuthInfo[YetuUser]]] = for {
        save <- authCodeAccessTokenService.saveAccessTokenToAuthInfo(testAccessToken.token, testUserInfo)
        retrieve <- authCodeAccessTokenService.findAuthInfoByAccessToken(testAccessToken.token)
      } yield retrieve

      whenReady(resultFuture) {
        result => result.get mustBe testUserInfo
      }

    }

  }

}
