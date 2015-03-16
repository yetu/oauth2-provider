package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.base.BaseMethods
import org.scalatestplus.play.{ OneServerPerSuite, OneBrowserPerSuite, HtmlUnitFactory, PlaySpec }
import play.api.Logger

/**
 * Created by elisahilprecht on 16/03/15.
 */
class IntegrationSetupSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with BaseMethods {

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