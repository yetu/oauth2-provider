package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }
import securesocial.core.services.SaveMode

import scala.concurrent.ExecutionContext.Implicits.global

abstract class BaseKeyServiceSpec extends DataServiceBaseSpec with ScalaFutures {

  s"The [$databaseImplementationName] Public Key Service" must {

    val testKey = YetuPublicKey("rsa-ssh ASDFDGHEGWEAFSASDFDGHEGWEAFSASDFDGHEGWEAFSASDFDGHEGWEAFS...")

    "store and retrieve a key" in {

      val result = for {
        delete <- personService.deleteUser(testUser.userId)
        user <- personService.save(testUser, SaveMode.SignUp)
        store <- publicKeyService.storeKeyF(testUser.userId, testKey)
        retrieve <- publicKeyService.getKeyF(testUser.userId)
      } yield retrieve

      whenReady(result, timeout(Span(5, Seconds))) {
        response =>
          response mustEqual Some(testKey)
          personService.deleteUser(testUser.userId)
      }
    }

  }
}

class LDAPKeyServiceITSpec extends BaseKeyServiceSpec with IntegrationTestRegistry

class MemoryKeyServiceSpec extends BaseKeyServiceSpec with TestRegistry