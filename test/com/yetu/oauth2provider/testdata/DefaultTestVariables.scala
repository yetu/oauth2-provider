package com.yetu.oauth2provider.testdata

import java.util.Date
import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.Temp.AuthInformation
import com.yetu.oauth2provider.oauth2.models.{ IdentityId, YetuUser }
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import securesocial.controllers.UserAgreement
import securesocial.core.{ PasswordInfo, AuthenticationMethod }

import scalaoauth2.provider.{ AccessToken, AuthInfo }

trait DefaultTestVariables {

  val integrationTestClientId = "integrationtest"
  val integrationTestSecret = "akbahskbskfbdsushvbgeri8374293fhe"

  //Password is 1234

  val testUserEmail = "test@test.test"
  val testUser = YetuUser(IdentityId(testUserEmail, "userpass"), "1231313131", "John", "Smith", "John Smith", Some(testUserEmail), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)), userAgreement = Some(UserAgreement(true)))
  val testUser2 = YetuUser(IdentityId("test2@test2.test2", "userpass"), "1231313222131", "John2", "Smith2", "John Smith2", Some("test2@test2.test2"), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)), userAgreement = Some(UserAgreement(true)))

  val testUserWithoutUserAgreement = YetuUser(IdentityId(testUserEmail, "userpass"), "1231313131", "John", "Smith", "John Smith", Some(testUserEmail), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)), userAgreement = None)

  val testUserPassword = "1234"
  val testAuthCode = "FDdrVgoQo2"
  val testClientId = "testClientId"
  val scopeOption = Some(SCOPE_BASIC)
  val testAccessToken: AccessToken = new AccessToken("bMqOIj86jKZVbo_kvJMG", Some("REFRESH"), scopeOption, Some(1234532L), new Date(System.currentTimeMillis()))
  val testUserInfo: AuthInformation = new AuthInformation(testUser, Some(testClientId), scopeOption, None)
  val testUserInfoWithScopeId: AuthInformation = new AuthInformation(testUser, Some(testClientId), Some(Config.SCOPE_ID), None)

  val loginUrlWithUserPass = "/authenticate/userpass"

  val loginUrlWithSignedHttp = "/authenticate/SignatureAuthentication"

  val updateUrl = "/profile"
  val healthUrl = "/health"

  val setupRegistrationUrl = "/setup/registration"
  val setupConfirmMailUrl = "/setup/confirmmail"
  val setupConfirmedMailUrl = "/setup/confirmedmail"
  val setupDownloadUrl = "/setup/download"

  val signupUrl = "/signup"
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
    val testUserInfo = new AuthInformation(user, Some(clientId), scopeOption, Some(redirectURIs.head))
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
