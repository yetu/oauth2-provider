package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }

//TODO: implement permissions correctly and make sure this test leaves no traces behind.
abstract class BasePermissionsServiceSpec extends DataServiceBaseSpec {
  //  s"The [$databaseImplementationName] Permission Service" must {
  //    val clientPermission = ClientPermission("123456", Some(List("scope1")))
  //    "delete, store and retrieve a permissions " in {
  //      personService.deleteUser(testUser.userId)
  //      permissionService.deletePermission(testUser.userId, clientPermission.clientId)
  //      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
  //      permissionService.savePermission(testUser.userId, clientPermission, true)
  //      val retrieved = permissionService.findPermission(testUser.userId, clientPermission.clientId)
  //      retrieved.get mustEqual clientPermission
  //      permissionService.deletePermission(testUser.userId, clientPermission.clientId)
  //      personService.deleteUser(testUser.userId)
  //    }
  //  }
}

class LDAPPermissionsServiceITSpec extends BasePermissionsServiceSpec with IntegrationTestRegistry

class MemoryPermissionsServiceSpec extends BasePermissionsServiceSpec with TestRegistry
