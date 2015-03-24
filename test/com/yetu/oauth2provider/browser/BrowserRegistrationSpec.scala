package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.oauth2.models.YetuUser

class BrowserRegistrationSpec extends BrowserBaseSpec {

  def register(password: String, email: String) = {
    val firstNameInputField = find(name("firstName"))
    click on firstNameInputField.value
    pressKeys("testFirstName")
    val lastNameInputField = find(name("lastName"))
    click on lastNameInputField.value
    pressKeys("testLastName")
    val emailInputField = find(name("email"))
    click on emailInputField.value
    pressKeys(email)
    val password1InputField = find(name("password.password1"))
    click on password1InputField.value
    pressKeys(password)
    val password2InputField = find(name("password.password2"))
    click on password2InputField.value
    pressKeys(password)
    click on find(tagName("button")).value
  }

  "Registration page without gateway" must {
    s"open $signupUrl when clicking on confirm button and add user when confirming with link in email" in {
      clearMailTokensInMemory()
      clearUsersFromMemory()

      //registration
      log("registration email")
      go to (s"http://localhost:$port$signupUrl")
      find(name("signup")) must be('defined)
      register(browserTestUserPassword, testUserEmail)
      val confirmMailHeader = find(name("confirmmail"))
      confirmMailHeader must be('defined)

      //confirming email
      log("confirming email")
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