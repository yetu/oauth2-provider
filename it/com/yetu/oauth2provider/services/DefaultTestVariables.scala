package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.oauth2.models.{IdentityId, YetuUser}
import securesocial.core.{PasswordInfo, AuthenticationMethod}

trait DefaultTestVariables {
  val integrationTestClientId = "integrationtest"
  val integrationTestSecret = "akbahskbskfbdsushvbgeri8374293fhe"

  //Password is 1234

  val testUserEmail = "test22@test.test"
  val testUser = YetuUser(IdentityId(testUserEmail, "userpass"), "1231313131", "John", "Smith", "John Smith", Some(testUserEmail), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)))
  val testUser2 = YetuUser(IdentityId("test2@test2.test2", "userpass"), "1231313222131", "John2", "Smith2", "John Smith2", Some("test2@test2.test2"), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)))

}
