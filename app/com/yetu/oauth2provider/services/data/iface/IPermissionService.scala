package com.yetu.oauth2provider.services.data.iface

import com.yetu.oauth2provider.oauth2.models.ClientPermission

trait IPermissionService {

  def savePermission(email: String, clientPermission: ClientPermission, ignoreEntryAlreadyExists: Boolean = false): Unit

  def deletePermission(email: String, clientId: String)

  def findPermission(userId: String, clientId: String): Option[ClientPermission]

}
