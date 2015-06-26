package com.yetu.oauth2provider.services.data.memory

import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.services.data.interface.IClientService

import scala.concurrent.Future

class MemoryClientService extends IClientService {

  import MemoryClientService.clients

  override def saveClient(client: OAuth2Client) = {
    Future.successful(clients += (client.clientId -> client))
  }

  override def findClient(clientId: String) = {
    Future.successful(clients.get(clientId))
  }

  override def deleteClient(client: OAuth2Client) = deleteClient(client.clientId)

  override def deleteClient(clientId: String) = Future.successful(clients -= clientId)

}

object MemoryClientService {
  var clients = Map[String, OAuth2Client]()
}
