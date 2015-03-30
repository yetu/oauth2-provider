package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.base.{ TestGlobal, BaseMethods }
import com.yetu.oauth2provider.oauth2.models.YetuUser
import org.scalatestplus.play.{ HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec }
import play.api.test.FakeApplication

/**
 * Created by elisahilprecht on 19/03/15.
 */
class BrowserSetupRegistrationSpec extends BrowserBaseSpec {

  "Registration page without gateway" must {
    s"open $setupRegistrationUrl" in {
      go to (s"http://localhost:$port$setupRegistrationUrl")
      find(name("setupSignup")) must be('defined)
      //
      //      clearMailTokensInMemory()
      //      clearUsersFromMemory()
      //
      //      //registration
      //      log("registration email")
      //      go to (s"http://localhost:$port$signupUrl")
      //      find(name("signup")) must be('defined)
      //      register(browserTestUserPassword, testUserEmail)
      //      val confirmMailHeader = find(name("confirmmail"))
      //      confirmMailHeader must be('defined)
      //
      //      //confirming email
      //      log("confirming email")
      //      val token = getMailTokenFromMemory
      //      go to (s"http://localhost:$port$signupUrl/$token")
      //
      //      val confirmMailSuccessHeader = find(name("signupsuccess"))
      //      confirmMailSuccessHeader must be('defined)
      //
      //      log("check if user is added to MemoryPersonService")
      //      val user: Option[YetuUser] = personService.findYetuUser(testUserEmail)
      //      user must be('defined)

    }
  }

  //    "still open signup page when passing a wrong token for confirming testUserEmail" in {
  //      val wrongToken = "fdjsbr";
  //      go to (s"http://localhost:$port$signupUrl/$wrongToken")
  //      find(name("signup")) must be('defined)
  //    }
  //  }
  //
  //  "Confirmed mail page" must {
  //    "have title called 'Successfully confirmed mail'" in {
  //      go to (s"http://localhost:$port" + setupConfirmedMailUrl)
  //      pageTitle mustBe "Successfully confirmed mail"
  //    }
  //    "show download page when clicking on next button" in {
  //      val nextButton = find(id("next_button"))
  //      nextButton must be ('defined)
  //      click on nextButton.value
  //      pageTitle mustBe "Download"
  //    }
}
