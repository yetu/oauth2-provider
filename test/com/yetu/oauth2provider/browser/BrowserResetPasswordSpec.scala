package com.yetu.oauth2provider.browser

class BrowserResetPasswordSpec extends BrowserBaseSpec {

  "Password reset page" must {
    "reset browserTestUserPassword" in {
      clearMailTokensInMemory

      log("create user")
      personService.addNewUser(testUser)

      //start password reset
      log("request change pw")
      go to (s"http://localhost:$port$passwordResetUrl")
      find(name("startpwreset")) must be('defined)
      val emailInputField = find(name("email"))
      click on emailInputField.value
      pressKeys(testUserEmail)
      click on find(tagName("button")).value

      //password reset
      log("do password change")
      val token = getMailTokenFromMemory
      go to (s"http://localhost:$port$passwordResetUrl/$token")
      find(name("pwreset")) must be('defined)
      val password1InputField = find(name("password.password1"))
      val password2InputField = find(name("password.password2"))
      click on password1InputField.value
      pressKeys(browserTestUserPassword)
      click on password2InputField.value
      pressKeys(browserTestUserPassword)

      click on find(tagName("button")).value

      find(name("login")) must be('defined)
    }
  }

}
