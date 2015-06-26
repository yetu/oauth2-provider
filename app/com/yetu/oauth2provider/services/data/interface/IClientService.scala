package com.yetu.oauth2provider
package services
package data
package interface

import com.yetu.oauth2provider.oauth2.models.OAuth2Client

import scala.concurrent.Future

trait IClientService {
  def saveClient(client: OAuth2Client): Future[Unit]

  def findClient(clientId: String): Future[Option[OAuth2Client]]

  def deleteClient(client: OAuth2Client): Future[Unit]

  def deleteClient(clientId: String): Future[Unit]
}
