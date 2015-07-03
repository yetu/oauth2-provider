package com.yetu.oauth2provider.services.data.memory

import com.yetu.oauth2provider.oauth2.models.ClientScopes
import com.yetu.oauth2provider.services.data.interface.IPermissionService
import play.api.Logger

import scala.concurrent.Future

object MemoryPermissionService {

  var permissions = Map[EmailClient, ClientScopes]()
}

class MemoryPermissionService extends IPermissionService {

  val logger = Logger(this.getClass)
  import MemoryPermissionService.permissions

  override def savePermission(email: String, clientPermission: ClientScopes, amend: Boolean = false): Future[Unit] = {
    logger.debug(s"save permission $email -> ${clientPermission.clientId}")
    Future.successful(permissions += EmailClient(email, clientPermission.clientId) -> clientPermission)
  }

  override def deletePermission(email: String, clientId: String): Future[Unit] = {
    logger.debug(s"delete permission $email -> $clientId")
    Future.successful(permissions -= EmailClient(email, clientId))
  }

  override def findPermission(email: String, clientId: String): Future[Option[ClientScopes]] = {
    Future.successful(findPermissionEntry(email, clientId).map(_._2))
  }

  private def findPermissionEntry(email: String, clientId: String) = {
    permissions.find(_._1 == EmailClient(email, clientId))
  }
}

case class EmailClient(email: String, clientId: String)