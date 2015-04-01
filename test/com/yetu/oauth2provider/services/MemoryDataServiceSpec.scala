package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.base.BaseSpec
import securesocial.core.services.SaveMode

class MemoryDataServiceSpec extends BaseSpec {

  "store and retrieve a user " in {

    // TODO: find a way to share code between test and it folders.

    personService.save(testUser.toBasicProfile, SaveMode.SignUp)
    val Some(yetuUser) = personService.findYetuUser(testUser.userId)

    yetuUser.email mustEqual testUser.email
    yetuUser.firstName mustEqual testUser.firstName
    yetuUser.passwordInfo mustEqual testUser.passwordInfo
    yetuUser.userAgreement mustEqual testUser.userAgreement

  }

}
