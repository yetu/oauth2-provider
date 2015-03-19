package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.base.{BaseMethods, TestGlobal}
import com.yetu.oauth2provider.services.data.MemoryMailTokenService
import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}
import play.api.test.FakeApplication
import securesocial.core.providers.MailToken

/**
 * Created by elisahilprecht on 16/03/15.
 */
class BrowserRegistrationSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with BaseMethods {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal),
      additionalConfiguration = Map("securesocial.ssl" -> "false"))

  lazy val password = "password"
  lazy val email = "test@test.de"

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

  def clearMailTokensInMemory = {
    MemoryMailTokenService.mailTokens = Map[String, MailToken]()
  }

  def getMailTokenFromMemory: String = {
    val tokens = MemoryMailTokenService.mailTokens
    tokens.head._1
  }

  "Registration page without gateway" must {
    "open $signupUrl when clicking on confirm button" in {
      clearMailTokensInMemory

      //registration
      log("confirming email")
      go to (s"http://localhost:$port$signupUrl")
      find(name("signup")) must be ('defined)
      register(password, email)
      find(name("confirmmail")) must be ('defined)

      //confirming email
      log("confirming email")
      val token = getMailTokenFromMemory
      go to (s"http://localhost:$port$signupUrl/$token")
      find(name("signupsuccess")) must be ('defined)
    }
    "still open signup page when passing a wrong token for confirming email" in {
      val wrongToken = "fdjsbr";
      go to (s"http://localhost:$port$signupUrl/$wrongToken")
      find(name("signup")) must be ('defined)
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
      val password1InputField = find(name("password.password1"));
      val password2InputField = find(name("password.password2"));
      click on password1InputField.value
      pressKeys(password)
      click on password2InputField.value
      pressKeys(password)
      log(pageSource)

      click on find(tagName("button")).value
    }
  }
}