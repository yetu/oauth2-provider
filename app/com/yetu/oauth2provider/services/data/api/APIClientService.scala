package com.yetu.oauth2provider.services.data.api

import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.services.data.interface.IClientService

class APIClientService() extends IClientService {

  def saveClient(client: OAuth2Client, ignoreEntryAlreadyExists: Boolean): Unit = {

  }

  def findClient(clientId: String): Option[OAuth2Client] = {
    None
  }

  def deleteClient(client: OAuth2Client) = {

  }

  def deleteClient(clientId: String) = {

  }

}
