package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }
import com.yetu.oauth2provider.services.data.iface.IPersonService
import com.yetu.oauth2provider.utils.DateUtility._
import org.joda.time.DateTime
import securesocial.core.PasswordInfo
import securesocial.core.services.SaveMode

/*
 * This test class is extended below to run the same tests against the in-memory and the LDAP implementations.
 */
abstract class UserServiceBase extends DataBaseSpec {

  override def beforeEach {
    personService.deleteUser(testUser.identityId.userId)
  }

  override def afterEach {
    personService.deleteUser(testUser.identityId.userId)
  }

  s"The [$databaseImplementationName] User Service" must {

    "store and retrieve a user " in {
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      val Some(yetuUser) = personService.findYetuUser(testUser.userId)

      yetuUser.email mustEqual testUser.email
      yetuUser.firstName mustEqual testUser.firstName
      yetuUser.passwordInfo mustEqual testUser.passwordInfo
      dateToString(yetuUser.userAgreement.get.acceptTermsAndConditionsDate.toDate) mustEqual dateToString(DateTime.now().toDate)
      yetuUser.userAgreement.get.acceptTermsAndConditions mustEqual true
    }

    "store an old user without userAgreement and retrieve a valid user " in {
      personService.save(testUserWithoutUserAgreement.toBasicProfile, SaveMode.SignUp)
      val Some(yetuUser) = personService.findYetuUser(testUserWithoutUserAgreement.userId)

      yetuUser.email mustEqual testUserWithoutUserAgreement.email
      yetuUser.firstName mustEqual testUserWithoutUserAgreement.firstName
      yetuUser.passwordInfo mustEqual testUserWithoutUserAgreement.passwordInfo
      yetuUser.userAgreement mustEqual testUserWithoutUserAgreement.userAgreement
      yetuUser.userAgreement mustEqual None

    }

    "store and retrieve a user with registration date " in {
      //registration date is automatically generated in LDAP

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

      val Some(retrieved) = personService.findYetuUser(newUserPassObject.userId)

      retrieved.passwordInfo mustEqual newUserPassObject.passwordInfo
      retrieved.passwordInfo must not be testUser.passwordInfo

    }

  }

}

/**
 * These tests extend the IntegrationTestRegistry and use the real configured LDAP
 *
 * requires a connection to LDAP with valid credentials:
 *
 * ldap {
 * username="cn=..."
 * password="..."
 * hostname="..."
 * port=1389
 * }
 *
 * set this in your conf/application-integrationtest.conf
 *
 */
class LDAPUserServiceITSpec extends UserServiceBase with IntegrationTestRegistry

class MemoryUserServiceSpec extends UserServiceBase with TestRegistry

