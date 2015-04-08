package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.controllers.setup.SetupController._

class BrowserSetupRegistrationSpec extends BrowserBaseSpec {

  val fullSetupRegistrationUrl = s"http://localhost:$port$setupRegistrationUrl"
  val fullSetupConfirmMailUrl = s"http://localhost:$port$setupConfirmMailUrl"
  val fullSetupConfirmedMailUrl = s"http://localhost:$port$setupConfirmedMailUrl"
  val fullSetupDownloadUrl = s"http://localhost:$port$setupDownloadUrl"

  def fullMailTokenUrl(token: String) = {
    s"http://localhost:$port$signupUrl/$token"
  }

  s"(Gateway-) Registration flow page at $setupRegistrationUrl" must {

    s"open $setupRegistrationUrl" in {
      go to fullSetupRegistrationUrl
      currentUrl mustEqual fullSetupRegistrationUrl
      find(name("setupSignup")) must be('defined)
    }

    s"open $setupRegistrationUrl and have '$UserNotRegistered' as the default radio button selection" in {
      go to fullSetupRegistrationUrl

      radioButtonGroup(UserRegistrationStatus).value mustEqual UserNotRegistered
    }

    s"open $setupRegistrationUrl and have 'agreement[]' checkbox unselected" in {
      go to fullSetupRegistrationUrl

      checkbox("agreement[]").isSelected must be(false)
    }

    "go to download page when selecting already registered" in {
      go to fullSetupRegistrationUrl

      radioButtonGroup(UserRegistrationStatus).value = UserAlreadyRegistered
      submit()

      currentUrl mustEqual fullSetupDownloadUrl
      find(name("setupDownload")) must be('defined)
    }

    "not register without filling out forms and should give error messages on fields" in {
      go to fullSetupRegistrationUrl

      radioButtonGroup(UserRegistrationStatus).value = UserNotRegistered
      submit()

      currentUrl mustEqual fullSetupRegistrationUrl

      //TODO: this code is not understandable,
      //TODO: what is this magic number 6 ? And why is it 6? What does it mean?
      //TODO: can we write this code in another way?
      val helpInlines = findAll(className("help-inline"))
      var counter = 0
      for (helpInline <- helpInlines) {
        if (counter > 0 && counter < 6) {
          helpInline must be('displayed)
        }
        counter = counter + 1
      }
      find(name("setupSignup")) must be('defined)
    }

    "not register without accepting terms and conditions and stay on the same page" in {
      go to fullSetupRegistrationUrl

      checkbox("agreement[]").clear()
      register(browserTestUserPassword, testUserEmail)

      currentUrl mustEqual fullSetupRegistrationUrl

      //TODO: check for an error message to be displayed.

    }

    def createNewUserThroughGatewaySetupProcess() = {
      go to fullSetupRegistrationUrl
      checkbox("agreement[]").select()
      register(browserTestUserPassword, testUserEmail)
    }

    s"go to confirm mail page if registration is correct" in {
      createNewUserThroughGatewaySetupProcess()

      currentUrl mustEqual fullSetupConfirmMailUrl
    }

    s"go to confirmed mail page when clicking the link in the email" in {
      createNewUserThroughGatewaySetupProcess()

      go to fullMailTokenUrl(getMailTokenFromMemory)

      currentUrl mustEqual fullSetupConfirmedMailUrl

    }

  }

  //  find(name("login")) must be('defined)
  //  //replace by that
  //  //find(name("confirmmailSetup")) must be('defined)
  //
  //  //confirming email
  //  log("confirming email")
  //  val token = getMailTokenFromMemory
  //  go to (s"http://localhost:$port$setupConfirmMailUrl/$token")
  //
  //  val confirmMailSuccessHeader = find(name("setupConfirmmail"))
  //  confirmMailSuccessHeader must be('defined)

  //add when it is implemented
  //      log("check if user is added to MemoryPersonService")
  //      val user: Option[YetuUser] = personService.findYetuUser(testUserEmail)
  //      user must be('defined)

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
