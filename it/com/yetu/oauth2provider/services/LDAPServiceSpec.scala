package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ClientPermission, IdentityId, OAuth2Client, YetuUser}
import com.yetu.oauth2provider.registry.{IntegrationTestRegistry}
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.DateUtility._
import org.joda.time.DateTime
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import securesocial.core.services.SaveMode
import securesocial.core.{AuthenticationMethod, PasswordInfo}




/**
 * These tests extend the IntegrationTestRegistry and use the real configured LDAP
 *
 * requires a connection to LDAP with valid credentials:
 *
 * ldap {
 *   username="cn=..."
 *   password="..."
 *   hostname="..."
 *   port=1389
 * }
 *
 */
class LDAPServiceSpec extends LDAPBaseSpec {

  override def beforeEach {
    personService.deleteUser(testUser.identityId.userId)
  }

  "The LDAP user service" must {
    "store and retrieve a user " in {
      personService.deleteUser(testUser.identityId.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      val retrieved = personService.findYetuUser(testUser.userId)

      retrieved.isDefined mustBe true

      retrieved.get.email mustEqual testUser.email
      retrieved.get.firstName mustEqual testUser.firstName
      retrieved.get.passwordInfo mustEqual testUser.passwordInfo
      personService.deleteUser(testUser.identityId.userId)
    }

    "store and retrieve a user with registration date " in {
      //registration date is automatically generated in LDAP

      personService.deleteUser(testUser.identityId.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      val retrieved = personService.findYetuUser(testUser.userId)

      retrieved.isDefined mustBe true

      retrieved.get.registrationDate.get must not be None
      dateToString(retrieved.get.registrationDate.get) mustEqual dateToString(DateTime.now().toDate)
    }






    "change password of user " in {

      personService.deleteUser(testUser.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      val newUserPassObject = testUser.copy(passwordInfo = Some(PasswordInfo("bcrypt", "$2a$10$huRtPOgtcSMvaYiznS3IG.8elJVBvSCDXUD11EXK6FLZqw5nL7iiO", None)))
      personService.save(newUserPassObject.toBasicProfile, SaveMode.PasswordChange)

      val retrieved = personService.findYetuUser(newUserPassObject.userId)

      retrieved.get.passwordInfo mustEqual newUserPassObject.passwordInfo
      retrieved.get.passwordInfo must not be testUser.passwordInfo

      personService.deleteUser(testUser.identityId.userId)

    }

  }



}
