package com.yetu.oauth2provider.ldap

import com.yetu.oauth2provider.oauth2.models.{ IdentityId, YetuUser }
import com.yetu.oauth2provider.registry.IntegrationTestRegistry
import com.yetu.oauth2provider.testdata.DefaultTestVariables
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }
import securesocial.controllers.UserAgreement
import securesocial.core.{ PasswordInfo, AuthenticationMethod }

class LDAPBaseSpecITSpec extends PlaySpec
    with OneAppPerSuite
    with IntegrationTestRegistry
    with DefaultTestVariables
    with BeforeAndAfterEach {

  override val testUserEmail = "test987654@test.test"
  override val testUser = YetuUser(IdentityId(testUserEmail, "userpass"), "1231313131", "John", "Smith", "John Smith", Some(testUserEmail), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$xZfTWeapL3blF3dA9mgUbeAAmCBLYC2HfOLVENFbJw4bC3X3NDhHS", None)), userAgreement = Some(UserAgreement(true)))

}
