package com.yetu.oauth2provider.services.data.interface

import com.yetu.oauth2provider.oauth2.models.ClientPermission

trait IPermissionService {

  def savePermission(uuid: String, clientPermission: ClientPermission)

  def deletePermission(uuid: String, clientId: String)

  def findPermission(userId: String, clientId: String): Option[ClientPermission]

}
