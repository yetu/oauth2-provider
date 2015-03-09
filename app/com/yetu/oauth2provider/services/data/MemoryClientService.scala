package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.{ ClientPermission, OAuth2Client }
import com.yetu.oauth2provider.services.data.iface.IClientService
import play.api.Logger

class MemoryClientService extends IClientService {

  import MemoryClientService.clients

  override def saveClient(client: OAuth2Client, ignoreEntryAlreadyExists: Boolean): Unit = {
    clients += (client.clientId -> client)
  }

  override def findClient(clientId: String): Option[OAuth2Client] = {

    clients.get(clientId)
  }

  override def deleteAllClients(): Unit = {
    Logger.warn("delete All Clients was executed")
    clients = Map[String, OAuth2Client]()
  }

  override def deleteClient(client: OAuth2Client) = clients -= client.clientId

  override def deleteClient(clientId: String) = clients -= clientId

}

object MemoryClientService {

  var clients = Map[String, OAuth2Client]()
}
