package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.{ YetuUser, ClientPermission }
import com.yetu.oauth2provider.services.data.iface.IPermissionService

import play.api.Logger

class MemoryPermissionService extends IPermissionService {

  val logger = Logger(this.getClass())
  import MemoryPermissionService.permissions

  override def savePermission(email: String, clientPermission: ClientPermission, ignoreEntryAlreadyExists: Boolean): Unit = {
    logger.debug(s"save permission $email -> ${clientPermission.clientId}")
    permissions += EmailClient(email, clientPermission.clientId) -> clientPermission
  }

  override def deletePermission(email: String, clientId: String): Unit = {
    logger.debug(s"delete permission $email -> ${clientId}")
    permissions -= EmailClient(email, clientId)
  }

  override def findPermission(email: String, clientId: String): Option[ClientPermission] = {
    val p = findPermissionEntry(email, clientId).map(_._2)
    p
  }

  private def findPermissionEntry(email: String, clientId: String) = {
    val x = permissions.find(_._1 == EmailClient(email, clientId))
    x
  }
}

case class EmailClient(email: String, clientId: String)

object MemoryPermissionService {

  var permissions = Map[EmailClient, ClientPermission]()
}
