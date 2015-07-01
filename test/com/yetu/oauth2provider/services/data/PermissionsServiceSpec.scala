package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ IntegrationTestRegistry, TestRegistry }
import securesocial.core.services.SaveMode

//TODO: fix this test after getting rid of LDAP
abstract class BasePermissionsServiceSpec extends DataServiceBaseSpec {

  override def beforeEach() {
    permissionService.deletePermission(testUser.userId, testClientId)
    personService.deleteUser(testUser.userId)
    clientService.deleteClient(testClientId)
    permissionService.deletePermission(testUser.userId, testClientId)
  }

  override def afterEach() = beforeEach()

  s"The [$databaseImplementationName] Permission Service" must {
    "delete, store and retrieve a permissions " in {
      personService.save(testUser, SaveMode.SignUp)
      clientService.saveClient(testClient)

      permissionService.savePermission(testUser.userId, testPermission)
      val retrieved = permissionService.findPermission(testUser.userId, testPermission.clientId)
      retrieved.get mustEqual testPermission
    }
  }
}

class LDAPPermissionsServiceITSpec extends BasePermissionsServiceSpec with IntegrationTestRegistry

class MemoryPermissionsServiceSpec extends BasePermissionsServiceSpec with TestRegistry
