package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ IntegrationTestRegistry, TestRegistry }

//TODO: fix this test after getting rid of LDAP
abstract class BasePermissionsServiceSpec extends DataServiceBaseSpec {

  def permissionToUserLink: String = {
    testUser.uid
  }

  override def beforeEach() {
    permissionService.deletePermission(permissionToUserLink, testClientId)
    personService.deleteUser(permissionToUserLink)
    clientService.deleteClient(testClientId)
    permissionService.deletePermission(permissionToUserLink, testClientId)
  }

  override def afterEach() {
    permissionService.deletePermission(permissionToUserLink, testClientId)
    personService.deleteUser(permissionToUserLink)
    clientService.deleteClient(testClientId)
    permissionService.deletePermission(permissionToUserLink, testClientId)
  }

  //TODO: fix this test after getting rid of LDAP
  //  s"The [$databaseImplementationName] Permission Service" must {
  //    "delete, store and retrieve a permissions " in {
  //
  //      clientService.saveClient(testClient)
  //      personService.addNewUser(testUser)
  //      permissionService.savePermission(permissionToUserLink, testPermission)
  //      val retrieved = permissionService.findPermission(permissionToUserLink, testPermission.clientId)
  //      retrieved.get mustEqual testPermission
  //    }
  //  }
}

class LDAPPermissionsServiceITSpec extends BasePermissionsServiceSpec with IntegrationTestRegistry

class MemoryPermissionsServiceSpec extends BasePermissionsServiceSpec with TestRegistry
