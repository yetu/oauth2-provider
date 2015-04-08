package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import securesocial.core.services.SaveMode

abstract class BaseKeyServiceSpec extends DataServiceBaseSpec {

  s"The [$databaseImplementationName] Public Key Service" must {

    val testKey = YetuPublicKey("rsa-ssh ASDFDGHEGWEAFS...")

    "store and retrieve a key" in {

      personService.deleteUser(testUser.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)

      publicKeyService.storeKey(testUser.userId, testKey)
      publicKeyService.getKey(testUser.userId).value mustEqual (testKey)

      personService.deleteUser(testUser.userId)
    }

  }
}

class LDAPKeyServiceITSpec extends BaseKeyServiceSpec with IntegrationTestRegistry

class MemoryKeyServiceSpec extends BaseKeyServiceSpec with TestRegistry