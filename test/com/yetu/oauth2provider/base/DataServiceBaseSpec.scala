package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.services.data.interface.{ IClientService, IPermissionService, IPersonService, IPublicKeyService }
import com.yetu.oauth2provider.utils.AsyncBeforeAndAfterEach
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }

abstract class DataServiceBaseSpec extends PlaySpec
    with OneAppPerSuite
    with DefaultTestVariables
    with AsyncBeforeAndAfterEach {

  // override these by extending either TestRegistry or IntegrationTestRegistry
  def personService: IPersonService

  def publicKeyService: IPublicKeyService

  def permissionService: IPermissionService

  def clientService: IClientService

  def databaseImplementationName: String

}