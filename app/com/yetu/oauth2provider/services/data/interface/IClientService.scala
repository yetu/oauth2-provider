package com.yetu.oauth2provider
package services
package data
package interface

import com.yetu.oauth2provider.oauth2.models.OAuth2Client

trait IClientService {
  def saveClient(client: OAuth2Client, ignoreEntryAlreadyExists: Boolean = false): Unit

  def findClient(clientId: String): Option[OAuth2Client]

  def deleteClient(client: OAuth2Client)

  def deleteClient(clientId: String)
}
