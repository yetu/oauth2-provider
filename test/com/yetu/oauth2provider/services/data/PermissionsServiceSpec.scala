package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ IntegrationTestRegistry, TestRegistry }

//TODO: fix this test after getting rid of LDAP
abstract class BasePermissionsServiceSpec extends DataServiceBaseSpec {

  def permissionToUserLink: String = {
    testUser.uid
  }

  override def beforeEach() {
    permissionService.deletePermission(testUser.userId, testClientId)
    personService.deleteUser(testUser.email)
    clientService.deleteClient(testClientId)
    permissionService.deletePermission(permissionToUserLink, testClientId)
  }

  override def afterEach() {
    permissionService.deletePermission(testUser.userId, testClientId)
    personService.deleteUser(testUser.email)
    clientService.deleteClient(testClientId)
    permissionService.deletePermission(permissionToUserLink, testClientId)
  }

  s"The [$databaseImplementationName] Permission Service" must {
    "delete, store and retrieve a permissions " in {
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      clientService.saveClient(testClient)

      permissionService.savePermission(testUser.userId, testPermission)
      val retrieved = permissionService.findPermission(testUser.userId, testPermission.clientId)
      retrieved.get mustEqual testPermission
    }
  }
}

class LDAPPermissionsServiceITSpec extends BasePermissionsServiceSpec with IntegrationTestRegistry

class MemoryPermissionsServiceSpec extends BasePermissionsServiceSpec with TestRegistry
