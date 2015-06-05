package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.base.{ TestGlobal, BaseIntegrationSpec }
import com.yetu.oauth2provider.controllers.authentication.{ CustomCookieAuthenticatorBuilder, CustomCookieAuthenticator }
import com.yetu.oauth2provider.oauth2.models.YetuUser
import org.joda.time.DateTime
import org.scalatest.concurrent.{ AsyncAssertions, ScalaFutures }
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatestplus.play.OneAppPerSuite
import play.api.test.Helpers._
import play.api.test._
import securesocial.core.authenticator.{ CookieAuthenticatorBuilder, AuthenticatorStore, CookieAuthenticator, IdGenerator }

import scala.concurrent.ExecutionContext.Implicits.global

class RiakAuthenticatorITSpec extends BaseIntegrationSpec with ScalaFutures with AsyncAssertions with OneAppPerSuite {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal))

  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Millis))

  "RiakCacheService" must {
    "save and retrieve the session" in {

      val generator: IdGenerator = new IdGenerator.Default()
      val builder = new CustomCookieAuthenticatorBuilder[YetuUser](cookieAuthStore, generator)

      val now = DateTime.now()
      val expirationDate = now.plusMinutes(CookieAuthenticator.absoluteTimeout)

      val provider = builder.fromUser(testUser)
      whenReady(provider) {
        authenticator =>
          val retrieve = cookieAuthStore.find(authenticator.id)
          whenReady(retrieve) {
            result => result.get mustBe authenticator
          }
      }
    }

  }

}
