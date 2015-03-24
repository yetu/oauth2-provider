package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.base.{ BaseMethods, TestGlobal }
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.{ MemoryPersonService, MemoryMailTokenService }
import org.scalatestplus.play.{ HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec }
import play.api.test.FakeApplication
import securesocial.core.BasicProfile
import securesocial.core.providers.MailToken

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

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

  def clearMailTokensInMemory() = {
    MemoryMailTokenService.mailTokens = Map[String, MailToken]()
  }

  def getMailTokenFromMemory: String = {
    val tokens = MemoryMailTokenService.mailTokens
    tokens.head._1
  }

  "Registration page without gateway" must {
    "open $signupUrl when clicking on confirm button and add user when confirming with link in email" in {
      clearMailTokensInMemory

      //registration
      log("registration email")
      go to (s"http://localhost:$port$signupUrl")
      find(name("signup")) must be('defined)
      register(password, email)
      val confirmMailHeader = find(name("confirmmail"))
      confirmMailHeader must be('defined)

      //confirming email
      log("confirming email")
      val token = getMailTokenFromMemory
      go to (s"http://localhost:$port$signupUrl/$token")

      val confirmMailSuccessHeader = find(name("signupsuccess"))
      confirmMailSuccessHeader must be ('defined)

      log("check if user is added to MemoryPersonService")
      val user: Option[YetuUser] = personService.findYetuUser(email)
      user must be('defined)

    }
    "still open signup page when passing a wrong token for confirming email" in {
      val wrongToken = "fdjsbr";
      go to (s"http://localhost:$port$signupUrl/$wrongToken")
      find(name("signup")) must be('defined)
    }
  }

  "Password reset page" must {
    "reset password" in {
      clearMailTokensInMemory

      log("create user")
      personService.addNewUser(testUser)

      //start password reset
      log("request change pw")
      go to (s"http://localhost:$port" + passwordResetUrl)
      find(name("startpwreset")) must be ('defined)
      var emailInputField = find(name("email"))
      click on emailInputField.value
      pressKeys(email)
      click on find(tagName("button")).value

      //password reset
      log("do password change")
      val token = getMailTokenFromMemory
      val url = s"http://localhost:$port$passwordResetUrl/$token"
      go to (s"http://localhost:$port$passwordResetUrl/$token")
      find(name("pwreset")) must be ('defined)
      val password1InputField = find(name("password.password1"))
      val password2InputField = find(name("password.password2"))
      click on password1InputField.value
      pressKeys(password)
      click on password2InputField.value
      pressKeys(password)

      click on find(tagName("button")).value

      find(name("login")) must be ('defined)
    }
  }
}