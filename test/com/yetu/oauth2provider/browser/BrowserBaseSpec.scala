package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.base.{ TestGlobal, BaseMethods }
import org.scalatestplus.play.{ HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec }
import play.api.test.FakeApplication

class BrowserBaseSpec extends PlaySpec
    with OneServerPerSuite
    with OneBrowserPerSuite
    with HtmlUnitFactory
    with BaseMethods {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal),
      additionalConfiguration = Map(
        "securesocial.ssl" -> false,
        "smtp.mock" -> true,
        "smtp.host" -> "test.gmail.com",
        "smtp.port" -> 587,
        "smtp.ssl" -> false,
        "smtp.user" -> "test@test.com",
        "smtp.password" -> "test",
        "smtp.from" -> "test@yetu.me"
      ))

  val browserTestUserPassword = "password"

}
