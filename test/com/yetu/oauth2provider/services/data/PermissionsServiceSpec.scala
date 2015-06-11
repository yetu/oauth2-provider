package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.oauth2.models.ClientPermission
import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }
import securesocial.core.services.SaveMode

//TODO: implement permissions correctly and make sure this test leaves no traces behind.
abstract class BasePermissionsServiceSpec extends DataServiceBaseSpec {

  override def beforeEach {
    permissionService.deletePermission(testUser.uid, testClientId)
    personService.deleteUser(testUser.email.get)
    clientService.deleteClient(testClientId)
  }

  override def afterEach {
    permissionService.deletePermission(testUser.uid, testClientId)
    personService.deleteUser(testUser.email.get)
    clientService.deleteClient(testClientId)
  }

  s"The [$databaseImplementationName] Permission Service" must {
    "delete, store and retrieve a permissions " in {
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      clientService.saveClient(testClient)

      permissionService.savePermission(testUser.uid, testPermission)
      val retrieved = permissionService.findPermission(testUser.uid, testPermission.clientId)
      retrieved.get mustEqual testPermission
    }
  }
}

class LDAPPermissionsServiceITSpec extends BasePermissionsServiceSpec with IntegrationTestRegistry

class MemoryPermissionsServiceSpec extends BasePermissionsServiceSpec with TestRegistry
