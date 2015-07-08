package com.yetu.oauth2provider
package services

import com.yetu.oauth2provider.base.BaseSpec
import com.yetu.oauth2provider.controllers.authentication.providers.EmailPasswordProvider
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.{ Config, DateUtility }
import com.yetu.oauth2resource.model.User
import org.joda.time.DateTime
import play.api.Logger
import securesocial.core.{ AuthenticationMethod, PasswordInfo }

class ScopeServiceSpec extends BaseSpec {

  "Scope service" must {
    val testUid = "adhyuah-4564-afdsgf-435"
    val testFirstName = "peter"
    val testLastName = "parker"
    val testRegistrationDate = DateTime.now()

    val testUser = YetuUser(testUid,
      EmailPasswordProvider.EmailPassword,
      Some(testFirstName),
      Some(testLastName),
      Some(testFirstName + " " + testLastName),
      Some(testUserEmail),
      None,
      AuthenticationMethod("userPassword"),
      None,
      None,
      Some(PasswordInfo("bcrypt", "$2a$10$qHwUqmHA7.24IZFNL90ke.mvjXwznoBh1pGR8D5r1TJ1tf9vttLji", None)),
      None,
      Some(testRegistrationDate))

    "return an id and an email for scope basic" in {
      val result = scopeService.getInfoByScope(testUser, Config.SCOPE_BASIC)
      result match {
        case Some(User(Some(userId), _, _, Some(email), _, _, _, _)) =>
          userId mustEqual testUid
          email mustEqual testUserEmail
        case _ => fail()
      }
    }

    "return a firstname, lastname, email and registration date for scope registrationInfo" in {
      val result = scopeService.getInfoByScope(testUser, Config.SCOPE_REGISTRATION_INFO)
      result match {
        case Some(User(Some(userId), Some(firstName), Some(lastName), Some(email), Some(registrationDate), _, _, _)) =>
          userId mustEqual testUid
          firstName mustEqual testFirstName
          lastName mustEqual testLastName
          email mustEqual testUserEmail
          registrationDate mustEqual DateUtility.dateToUtcString(testRegistrationDate)
        case _ => fail()
      }
    }

    "return an id but not registration date for scope basic" in {
      val result = scopeService.getInfoByScope(testUser, Config.SCOPE_BASIC)
      Logger.info(result.toString)
      result match {
        case Some(User(Some(userId), _, _, _, None, _, _, _)) =>
          userId mustEqual testUser.userId
        case _ => fail()
      }
    }

    "return None for invalid scope" in {
      val result = scopeService.getInfoByScope(testUser, "asljkbiqrwbu")
      result mustBe None
    }

    "return a firstname, lastname, email and registration date for two scopes: basic + registrationInfo" in {

      val multiScope = List(Config.SCOPE_BASIC, Config.SCOPE_REGISTRATION_INFO).mkString(" ")
      val result = scopeService.getInfoByScope(testUser, multiScope)
      result match {
        case Some(User(Some(userId), Some(firstName), Some(lastName), Some(email), Some(registrationDate), _, _, _)) =>
          userId mustEqual testUid
          firstName mustEqual testFirstName
          lastName mustEqual testLastName
          email mustEqual testUserEmail
          registrationDate mustEqual DateUtility.dateToUtcString(testRegistrationDate)
        case _ => fail()
      }
    }

  }
}
