package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.base.BaseMethods
import org.scalatestplus.play.{ OneServerPerSuite, OneBrowserPerSuite, HtmlUnitFactory, PlaySpec }
import play.api.Logger

/**
 * Created by elisahilprecht on 16/03/15.
 */
class IntegrationSetupSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with BaseMethods {

  "Download page" must {
    "title must be 'Download'" in {
      go to (s"http://localhost:$port" + setupDownloadUrl)
      pageTitle mustBe "Download"
      Logger.info(pageSource)

    }
    "show new content when clicking on download" in {
      //TODO: why it is a windows user agent
      find(id("download_win1")) must be ('defined)
      click on find(id("download_win1")).value
      eventually { find(id("fullContainer")) must be ('defined) }
    }
  }
}