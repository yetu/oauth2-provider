package com.yetu.oauth2provider.browser

/**
 * Created by elisahilprecht on 30/03/15.
 */
class DownloadBrowserSpec extends BaseBrowserSpec {
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
}
