package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ IntegrationTestRegistry, TestRegistry }
import securesocial.core.services.SaveMode

abstract class BasePermissionsServiceSpec extends DataServiceBaseSpec {

  def permissionToUserLink: String = {
    // should be the UUID, not the email! //TODO: fix this test after getting rid of LDAP
    // testUser.uid
    testUser.email.get
  }

  override def beforeEach() {
    personService.deleteUser(permissionToUserLink)
    clientService.deleteClient(testClientId)
    permissionService.deletePermission(permissionToUserLink, testClientId)
  }

  override def afterEach() {
    permissionService.deletePermission(permissionToUserLink, testClientId)
    personService.deleteUser(permissionToUserLink)
    clientService.deleteClient(testClientId)
  }

  s"The [$databaseImplementationName] Permission Service" must {
    "delete, store and retrieve a permissions " in {

      clientService.saveClient(testClient)
      try {
        personService.addNewUser(testUser)
      } catch {
        //do nothing. LDAP delete of user seems not to work correctly. Obsolete as LDAP will not remain in use.
        case e: Exception =>
      }

      permissionService.savePermission(permissionToUserLink, testPermission)
      val retrieved = permissionService.findPermission(permissionToUserLink, testPermission.clientId)
      retrieved.get mustEqual testPermission
    }
  }
}

class LDAPPermissionsServiceITSpec extends BasePermissionsServiceSpec with IntegrationTestRegistry

class MemoryPermissionsServiceSpec extends BasePermissionsServiceSpec with TestRegistry
