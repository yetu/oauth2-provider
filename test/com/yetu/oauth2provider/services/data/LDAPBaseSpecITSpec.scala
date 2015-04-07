package com.yetu.oauth2provider.services.data

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

}

class DataBaseSpec extends PlaySpec
    with OneAppPerSuite
    with DefaultTestVariables
    with BeforeAndAfterEach {

}