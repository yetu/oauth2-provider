package com.yetu.oauth2provider.services.data.interface

import com.yetu.oauth2provider.oauth2.models.ClientPermission

import scala.concurrent.Future

trait IPermissionService {

  def savePermission(userId: String, clientPermission: ClientPermission, amend: Boolean = false): Future[Unit]

  def deletePermission(userId: String, clientId: String): Future[Unit]

  def findPermission(userId: String, clientId: String): Future[Option[ClientPermission]]

}
