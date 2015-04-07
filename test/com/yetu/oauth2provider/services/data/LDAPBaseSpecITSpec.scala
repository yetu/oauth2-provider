package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.{ IdentityId, YetuUser }
import com.yetu.oauth2provider.registry.IntegrationTestRegistry
import com.yetu.oauth2provider.services.data.iface.IPersonService
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

abstract class DataBaseSpec extends PlaySpec
    with OneAppPerSuite
    with DefaultTestVariables
    with BeforeAndAfterEach {

  // override me!
  def personService: IPersonService

  // override me!
  def databaseImplementationName: String

}