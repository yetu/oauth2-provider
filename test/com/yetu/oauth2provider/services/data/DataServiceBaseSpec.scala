package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.services.data.iface.{ IClientService, IPersonService, IPublicKeyService }
import com.yetu.oauth2provider.testdata.DefaultTestVariables
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }

abstract class DataServiceBaseSpec extends PlaySpec
    with OneAppPerSuite
    with DefaultTestVariables
    with BeforeAndAfterEach {

  // override these by extending either TestRegistry or IntegrationTestRegistry
  def personService: IPersonService

  def publicKeyService: IPublicKeyService

  def clientService: IClientService

  def databaseImplementationName: String

}