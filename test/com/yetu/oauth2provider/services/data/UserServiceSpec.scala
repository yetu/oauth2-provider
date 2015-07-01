package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ IntegrationTestRegistry, TestRegistry }
import com.yetu.oauth2provider.utils.DateUtility._
import org.joda.time.DateTime
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }
import securesocial.core.PasswordInfo
import securesocial.core.services.SaveMode

import scala.concurrent.ExecutionContext.Implicits.global

/*
 * This test class is extended below to run the same tests against the in-memory and the LDAP implementations.
 */
abstract class UserServiceBase extends DataServiceBaseSpec with ScalaFutures {

  s"The [$databaseImplementationName] User Service" must {

    "store and retrieve a user " in {

      val result = for {
        delete <- personService.deleteUser(testUser.userId)
        save <- personService.save(testUser.toBasicProfile, SaveMode.SignUp)
        retrieve <- personService.findUser(testUser.userId)
      } yield retrieve

      whenReady(result, timeout(Span(2, Seconds))) {
        yetuUser =>
          yetuUser.isDefined mustBe true
          yetuUser.get.email mustEqual testUser.email
          yetuUser.get.firstName mustEqual testUser.firstName
          yetuUser.get.passwordInfo mustEqual testUser.passwordInfo
          dateToString(yetuUser.get.userAgreement.get.acceptTermsAndConditionsDate.toDate) mustEqual dateToString(DateTime.now().toDate)
          yetuUser.get.userAgreement.get.acceptTermsAndConditions mustEqual true
      }
    }

    "store an old user without userAgreement and retrieve a valid user " in {

      val result = for {
        delete <- personService.deleteUser(testUserWithoutUserAgreement.userId)
        save <- personService.save(testUserWithoutUserAgreement.toBasicProfile, SaveMode.SignUp)
        retrieve <- personService.findUser(testUserWithoutUserAgreement.userId)
      } yield retrieve

      whenReady(result, timeout(Span(2, Seconds))) {
        yetuUser =>
          yetuUser.isDefined mustBe true
          yetuUser.get.email mustEqual testUserWithoutUserAgreement.email
          yetuUser.get.firstName mustEqual testUserWithoutUserAgreement.firstName
          yetuUser.get.passwordInfo mustEqual testUserWithoutUserAgreement.passwordInfo
          yetuUser.get.userAgreement mustEqual testUserWithoutUserAgreement.userAgreement
          yetuUser.get.userAgreement mustEqual None
      }
    }

    "store and retrieve a user with registration date " in {

      val result = for {
        delete <- personService.deleteUser(testUser.userId)
        save <- personService.save(testUser.toBasicProfile, SaveMode.SignUp)
        retrieve <- personService.findUser(testUser.userId)
      } yield retrieve

      whenReady(result, timeout(Span(2, Seconds))) {
        retrieved =>
          retrieved.isDefined mustBe true
          retrieved.get.registrationDate.get must not be None
          dateToString(retrieved.get.registrationDate.get) mustEqual dateToString(DateTime.now().toDate)
      }
    }

    "change password of user " in {

      val pw = PasswordInfo("bcrypt", "$2a$10$huRtPOgtcSMvaYiznS3IG.8elJVBvSCDXUD11EXK6FLZqw5nL7iiO", None)

      val result = for {
        delete <- personService.deleteUser(testUser.userId)
        save <- personService.save(testUser.toBasicProfile, SaveMode.SignUp)
        update <- personService.updatePasswordInfo(save, pw)
        retrieve <- personService.findUser(testUser.userId)
      } yield retrieve

      whenReady(result, timeout(Span(2, Seconds))) {
        retrieved =>
          retrieved.isDefined mustBe true
          retrieved.get.passwordInfo mustEqual Some(pw)
          retrieved.get.passwordInfo must not be Some(testUser.passwordInfo)
      }
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

