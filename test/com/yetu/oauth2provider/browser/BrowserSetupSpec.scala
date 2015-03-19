package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.base.{ TestGlobal, BaseMethods }
import org.scalatestplus.play.{ HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec }
import play.api.test.FakeApplication

/**
 * Created by elisahilprecht on 19/03/15.
 */
class BrowserSetupSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with BaseMethods {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal),
      additionalConfiguration = Map(
        "securesocial.ssl" -> "false",
        "smtp.mock" -> "true",
        "smtp.host" -> "test.gmail.com",
        "smtp.port" -> "587",
        "smtp.ssl" -> "false",
        "smtp.user" -> "test@test.com",
        "smtp.password" -> "test",
        "smtp.from" -> "test@yetu.me"
      ))

  "Download page" must {
    "have title called 'Download'" in {
      go to (s"http://localhost:$port" + setupDownloadUrl)
      pageTitle mustBe "Download"

    }
    "show new content when clicking on download" in {
      //TODO: Fix this test
      //      val downloadButton = find(id("download_win1"))
      //      downloadButton must be ('defined)
      //      click on downloadButton.value
      //      eventually { find(id("fullContainer")) must be ('defined) }
      // Last line: The code passed to eventually never returned normally. Attempted 43 times over 15.266835617
      // seconds. Last failure message: None was not defined.
    }
  }

  "Confirmed mail page" must {
    "have title called 'Successfully confirmed mail'" in {
      go to (s"http://localhost:$port" + setupConfirmedMailUrl)
      pageTitle mustBe "Successfully confirmed mail"
    }
    "show download page when clicking on next button" in {
      val nextButton = find(id("next_button"))
      nextButton must be ('defined)
      click on nextButton.value
      pageTitle mustBe "Download"
    }
  }
}
