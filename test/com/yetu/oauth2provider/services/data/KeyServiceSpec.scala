package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import org.scalatest.concurrent.ScalaFutures
import securesocial.core.services.SaveMode

import scala.concurrent.ExecutionContext.Implicits.global

abstract class BaseKeyServiceSpec extends DataServiceBaseSpec with ScalaFutures {

  s"The [$databaseImplementationName] Public Key Service" must {

    val testKey = YetuPublicKey("rsa-ssh ASDFDGHEGWEAFS...")

    "store and retrieve a key" in {

      personService.deleteUser(testUser.userId)
      personService.save(testUser, SaveMode.SignUp)

      val result = for {
        store <- publicKeyService.storeKeyF(testUser.userId, testKey)
        retrieve <- publicKeyService.getKeyF(testUser.userId)
      } yield retrieve

      whenReady(result) {
        response =>
          response mustEqual Some(testKey)
          personService.deleteUser(testUser.userId)
      }
    }

  }
}

class LDAPKeyServiceITSpec extends BaseKeyServiceSpec with IntegrationTestRegistry

class MemoryKeyServiceSpec extends BaseKeyServiceSpec with TestRegistry