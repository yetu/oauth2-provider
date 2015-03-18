package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.base.{ TestGlobal, BaseMethods }
import com.yetu.oauth2provider.services.data.MemoryMailTokenService
import org.scalatest.selenium.WebBrowser.click
import org.scalatestplus.play.{ OneServerPerSuite, OneBrowserPerSuite, HtmlUnitFactory, PlaySpec }
import play.api.test.FakeApplication
import securesocial.core.providers.MailToken

/**
 * Created by elisahilprecht on 16/03/15.
 */
class IntegrationSetupSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with BaseMethods {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal),
      additionalConfiguration = Map("securesocial.ssl" -> "false"))

  lazy val password = "password"
  lazy val email = "test@test.de"

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