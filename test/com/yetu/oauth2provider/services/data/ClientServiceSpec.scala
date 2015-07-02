package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.registry.{ IntegrationTestRegistry, TestRegistry }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }

import scala.concurrent.ExecutionContext.Implicits.global

abstract class BaseClientServiceSpec extends DataServiceBaseSpec with ScalaFutures {

  s"The [$databaseImplementationName] client service" must {

    "delete, store and retrieve a client with one redirects, scope and grantType " in {
      val client = OAuth2Client("2223", "secret1", List("http://a"), Some(List("token")), "clientName", coreYetuClient = true)

      val retrieved = for {
        delete <- clientService.deleteClient(client)
        save <- clientService.saveClient(client)
        find <- clientService.findClient(client.clientId)
      } yield find

      whenReady(retrieved, timeout(Span(5, Seconds))) {
        result => result mustEqual Some(client)
      }
    }

    "delete, store and retrieve a client with multiple attributes " in {
      val client = OAuth2Client("2224", "secret2", List("http://a"), Some(List("token")), "clientName", coreYetuClient = true)

      val retrieved = for {
        delete <- clientService.deleteClient(client)
        save <- clientService.saveClient(client)
        find <- clientService.findClient(client.clientId)
      } yield find

      whenReady(retrieved, timeout(Span(5, Seconds))) {
        result => result mustEqual Some(client)
      }
    }

    "store a client multiple time must fail gracefully" ignore {
      //TODO: think in how to implement this kind of test.. maybe chance the return of save to Future[Result]
    }

    "return None if client not in database " in {
      whenReady(clientService.findClient("65678987654567898765"), timeout(Span(5, Seconds))) {
        result => result mustBe None
      }
    }
  }
}

class APIClientServiceITSpec extends BaseClientServiceSpec with IntegrationTestRegistry

class MemoryClientServiceSpec extends BaseClientServiceSpec with TestRegistry