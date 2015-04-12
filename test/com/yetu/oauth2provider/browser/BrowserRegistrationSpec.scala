package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.oauth2.models.YetuUser

class BrowserRegistrationSpec extends BrowserBaseSpec {

  s"Registration flow page at $signupUrl" must {
    s"open page at $signupUrl " in {
      go to (s"http://localhost:$port$signupUrl")

      find(name("signup")) must be('defined)
    }
    s"come back to $signupUrl when error on input fields" in {
      go to (s"http://localhost:$port$signupUrl")

      checkbox("agreement").clear()
      register(browserTestUserPassword, testUserEmail)

      find(name("signup")) must be('defined)
    }

    def sendRegistration = {
      checkbox("agreement").select()
      checkbox("agreement").isSelected must be(true)
      register(browserTestUserPassword, testUserEmail)
    }
    s"redirect to $loginUrlWithSignedHttp when user input is correct" in {
      go to (s"http://localhost:$port$signupUrl")

      sendRegistration

      val confirmMailHeader = find(name("confirmmail"))
      confirmMailHeader must be('defined)
    }

    s"open $confirmedSignUpUrl when user go to confirmation link and create user" in {
      go to (s"http://localhost:$port$signupUrl")

      sendRegistration

      val token = getMailTokenFromMemory
      go to (s"http://localhost:$port$signupUrl/$token")

      val confirmMailSuccessHeader = find(name("signupsuccess"))
      confirmMailSuccessHeader must be('defined)

      log("check if user is added to MemoryPersonService")
      val user: Option[YetuUser] = personService.findYetuUser(testUserEmail)
      user must be('defined)

    }

    "still open signup page when passing a wrong token for confirming testUserEmail" in {
      val wrongToken = "fdjsbr";
      go to (s"http://localhost:$port$signupUrl/$wrongToken")
      find(name("signup")) must be('defined)
    }
  }

}