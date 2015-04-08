package com.yetu.oauth2provider.browser

import com.yetu.oauth2provider.base.{ TestGlobal, BaseMethods }
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.{ MemoryPersonService, MemoryMailTokenService }
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{ HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec }
import play.api.test.FakeApplication
import securesocial.core.providers.MailToken

class BrowserBaseSpec extends PlaySpec
    with OneServerPerSuite
    with OneBrowserPerSuite
    with BeforeAndAfterEach
    with HtmlUnitFactory
    with BaseMethods {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal),
      additionalConfiguration = Map(
        "securesocial.ssl" -> false,
        "smtp.mock" -> true,
        "smtp.host" -> "test.gmail.com",
        "smtp.port" -> 587,
        "smtp.ssl" -> false,
        "smtp.user" -> "test@test.com",
        "smtp.password" -> "test",
        "smtp.from" -> "test@yetu.me"
      ))

  val browserTestUserPassword = "pasSw0rd" // bad password, but enough to pass the basic validation.

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
    submit()
  }

  def clearMailTokensInMemory() = {
    MemoryMailTokenService.mailTokens = Map[String, MailToken]()
  }

  def clearUsersFromMemory() = {
    MemoryPersonService.users = Map[String, YetuUser]()
  }

  def getMailTokenFromMemory: String = {
    val tokens = MemoryMailTokenService.mailTokens
    log(s"TOKENS: $tokens")
    tokens.head._1
  }

  override def beforeEach = {
    clearMailTokensInMemory()
    clearUsersFromMemory()
  }

}
