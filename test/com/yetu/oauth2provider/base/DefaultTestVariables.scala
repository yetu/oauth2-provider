package com.yetu.oauth2provider.base

import java.util.Date

import com.yetu.oauth2provider.controllers.authentication.providers.EmailPasswordProvider
import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ ClientScopes, OAuth2Client, YetuUser }
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import org.joda.time.DateTime
import play.api.libs.json.Json
import securesocial.controllers.UserAgreement
import securesocial.core.providers.MailToken
import securesocial.core.{ AuthenticationMethod, PasswordInfo }

import scalaoauth2.provider.{ AccessToken, AuthInfo }

trait DefaultTestVariables {

  val integrationTestClientId = "integrationtest"
  val integrationTestSecret = "akbahskbskfbdsushvbgeri8374293fhe"

  //Password is 1234

  val testUserEmail = "test@test.test"
  val testUser = YetuUser("73fe4f5c-1ffe-11e5-b5f7-727283247c7f", EmailPasswordProvider.EmailPassword, Some("John"), Some("Smith"), Some("John Smith"), Some(testUserEmail), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)), userAgreement = Some(UserAgreement(true)), registrationDate = Some(DateTime.now()))
  val testUser2 = YetuUser("73fe5628-1ffe-11e5-b5f7-727283247c7f", EmailPasswordProvider.EmailPassword, Some("John2"), Some("Smith2"), Some("John Smith2"), Some("test2@test2.test2"), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)), userAgreement = Some(UserAgreement(true)), registrationDate = Some(DateTime.now()))

  val testUserWithoutUserAgreement = YetuUser("73fe57ae-1ffe-11e5-b5f7-727283247c7f", EmailPasswordProvider.EmailPassword, Some("John"), Some("Smith"), Some("John Smith"), Some(testUserEmail), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)), userAgreement = None, registrationDate = Some(DateTime.now()))

  val testUserPassword = "1234"
  val testAuthCode = "FDdrVgoQo2"
  val testClientName = "testClientName"
  val testClientId = "testClientId"
  val testClientSecret = "testClientSecret"
  val testRedirectUri = "http://redirectUrl"
  val testGrantTypes = List("token", "authorization_code")
  val scopeOption = Some(SCOPE_BASIC)
  val testAccessToken: AccessToken = new AccessToken("bMqOIj86jKZVbo_kvJMG", Some("REFRESH"), scopeOption, Some(1234532L), new Date(System.currentTimeMillis()))
  val testUserInfo: AuthInfo[YetuUser] = new AuthInfo[YetuUser](testUser, Some(testClientId), scopeOption, None)
  val testUserInfoWithScopeId: AuthInfo[YetuUser] = new AuthInfo[YetuUser](testUser, Some(testClientId), Some(Config.SCOPE_ID), None)

  val testClient = OAuth2Client(testClientId, testClientSecret, List(testRedirectUri), Some(testGrantTypes), testClientName, coreYetuClient = false)
  val testPermission = ClientScopes(testClientId, Some(List("basic")))

  val testMailToken: MailToken = new MailToken("mail-token-uuid", testUser.email.get, DateTime.now(), DateTime.now(), true)

  val loginUrlWithUserPass = "/authenticate/userpass"

  val loginUrlWithSignedHttp = "/authenticate/SignatureAuthentication"

  val updateUrl = "/profile"
  val healthUrl = "/health"

  val setupRegistrationUrl = "/setup/registration"
  val setupConfirmMailUrl = "/setup/confirmmail"
  val setupConfirmedMailUrl = "/setup/confirmedmail"
  val setupConfirmedMailErrorUrl = "/setup/confirmedmailerror"
  val setupDownloadUrl = "/setup/download"

  val signupUrl = "/signup"
  val confirmedSignUpUrl = "/confirmedSignup"
  val passwordResetUrl = "/reset"

  val validateUrl = "/oauth2/validate"
  val infoUrl = "/oauth2/info"
  val authorizationUrl = "/oauth2/authorize"
  val implicitGrantFlow = "/oauth2/access_token_implicit"

  val accessTokenUrl = "/oauth2/access_token"
  val testStateParameter = "1234567ytrewqwerthjhgfdsdfgnbfdsdfghjhgfd"
  val defaultRedirectUrl = "http://dummyRedirectUrl"
  val defaultRedirectUrl2 = "http://dummyRedirectUrl2"

  def generateTestVariables(scope: String = Config.SCOPE_BASIC, user: YetuUser = testUser, clientId: String = testClientId, redirectURIs: List[String] = List(defaultRedirectUrl)) = {
    val scopeOption = Some(scope)
    val testUserInfo = new AuthInfo[YetuUser](user, Some(clientId), scopeOption, Some(redirectURIs.head))
    val testAccessToken: AccessToken = new AccessToken(s"$scope-randomnessAccessToken", Some("REFRESH"), scopeOption, Some(1234532L), new Date(System.currentTimeMillis()))
    (testUserInfo, testAccessToken)
  }

  val userUpdateData = """{
                         |    "firstName": "John",
                         |    "lastName": "Smith",
                         |    "contactInfo": {
                         |        "country": "Germany"
                         |    }
                         |}""".stripMargin
  val jsonUpdateData = Json parse userUpdateData
  val dataUpdateRequest = jsonUpdateData.as[DataUpdateRequest]

}

object DefaultTestVariables extends DefaultTestVariables
