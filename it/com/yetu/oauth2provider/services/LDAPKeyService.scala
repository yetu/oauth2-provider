package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.signature.models.YetuPublicKey
import securesocial.core.services.SaveMode

class LDAPKeyService extends LDAPBaseSpec{
  "The LDAP public key service" must {
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
