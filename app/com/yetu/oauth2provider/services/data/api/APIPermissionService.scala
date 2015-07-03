package com.yetu.oauth2provider.services.data.api

import com.yetu.oauth2provider.oauth2.models.ClientScopes
import com.yetu.oauth2provider.services.data.interface.IPermissionService
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.mvc.Http

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class APIPermissionService extends IPermissionService with APIHelper {

  def findPermission(userId: String, clientId: String) = {
    WS.url(urlForResource("permissions", userId + "/" + clientId, Version1)).get().map(response => {
      if (response.status == Http.Status.OK) {

        val json = Json.parse(response.body)
        val permittedScopes = json.as[List[String]]

        if (permittedScopes.nonEmpty) {
          Some(ClientScopes(clientId, Some(permittedScopes)))
        } else None

      } else None
    })
  }

  def savePermission(userId: String, clientPermission: ClientScopes, amend: Boolean) = {

    val permissionData = Json.obj(
      "amend" -> amend,
      "scopes" -> clientPermission.scopes
    )

    WS.url(urlForResource("authorize", userId + "/" + clientPermission.clientId, Version1))
      .post(permissionData)
      .map(response => {
        Unit
      })
  }

  def deletePermission(userId: String, clientId: String) = {
    WS.url(urlForResource("revoke", userId + "/" + clientId, Version1))
      .post(Json.arr())
      .map(_ => Unit)
  }

}
