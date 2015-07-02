package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.registry.{ IntegrationTestRegistry, TestRegistry }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }
import securesocial.core.services.SaveMode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class BasePermissionsServiceSpec extends DataServiceBaseSpec with ScalaFutures {

  override def beforeEach(): Future[Unit] = {
    for {
      deleteUser <- personService.deleteUser(testUser.userId)
      deleteClient <- clientService.deleteClient(testClientId)
    } yield deleteClient
  }

  override def afterEach(): Future[Unit] = beforeEach()

  s"The [$databaseImplementationName] Permission Service" must {
    "delete, store and retrieve permissions" in {

      val retrieved = for {
        savePerson <- personService.save(testUser, SaveMode.SignUp)
        saveClient <- clientService.saveClient(testClient)
        savePermission <- permissionService.savePermission(testUser.userId, testPermission)
        find <- permissionService.findPermission(testUser.userId, testPermission.clientId)
      } yield find

      whenReady(retrieved, timeout(Span(2, Seconds))) {
        result => result mustEqual Some(testPermission)
      }
    }
  }
}

class APIPermissionsServiceITSpec extends BasePermissionsServiceSpec with IntegrationTestRegistry

class MemoryPermissionsServiceSpec extends BasePermissionsServiceSpec with TestRegistry
