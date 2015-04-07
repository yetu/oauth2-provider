package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }

abstract class BaseClientServiceSpec extends DataServiceBaseSpec {

  s"The [$databaseImplementationName] client service" must {

    "delete, store and retrieve a client with one of each redirects, scope and grantType " in {
      val client = OAuth2Client("2223", "secret", List("http://a"), Some(List("code")), Some(List("scope1")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)
      clientService.saveClient(client)
      val retrieved = clientService.findClient(client.clientId)

      retrieved.get mustEqual client
    }

    "delete, store and retrieve a client with multiple attributes " in {
      val client = OAuth2Client("2224", "secret", List("http://a", "http://b"), Some(List("code", "bla")), Some(List("scope1", "scope2")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)
      clientService.saveClient(client)

      val retrieved = clientService.findClient(client.clientId)
      retrieved.get mustEqual client
    }

    "delete, store and retrieve a client with some attribute being None (at the moment all attributes are mandatory, ignoring test case)" ignore {
      val client = OAuth2Client("2225", "secret", List("http://a"), Some(List("code", "bla")), Some(List("scope1", "scope2")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)
      clientService.saveClient(client)

      val retrieved = clientService.findClient(client.clientId)
      retrieved.get mustEqual client
    }

    "store a client multiple time must fail gracefully if ignoreEntryAlreadyExists = true " in {
      val client = OAuth2Client("2224", "secret", List("http://a", "http://b"), Some(List("code", "bla")), Some(List("scope1", "scope2")), "clientName", coreYetuClient = true)
      clientService.deleteClient(client)
      clientService.saveClient(client, ignoreEntryAlreadyExists = true)
      clientService.saveClient(client, ignoreEntryAlreadyExists = true)

      //must not throw an exception.

    }

    " return None if client not in database " in {
      val retrieved = clientService.findClient("65678987654567898765")
      retrieved mustBe None
      //must not throw an exception.

    }

    "delete, store and retrieve a client with clientID as String " in {
      val client = OAuth2Client("delpes", "secret", List("http://a"), Some(List("code")), Some(List("scope1")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)

      clientService.saveClient(client)
      val retrieved = clientService.findClient(client.clientId)

      retrieved.get mustEqual client
    }

  }
}

class LDAPClientServiceITSpec extends BaseClientServiceSpec with IntegrationTestRegistry

class MemoryClientServiceSpec extends BaseClientServiceSpec with TestRegistry