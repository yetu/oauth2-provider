package com.yetu.oauth2provider.services.data.api

import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.services.data.interface.IClientService
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.mvc.Http

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class APIClientService extends IClientService with APIHelper with NamedLogger {

  override def saveClient(client: OAuth2Client) = {

    val clientJson = Json.obj(
      "id" -> client.clientId,
      "name" -> client.clientName,
      "secret" -> client.clientSecret,
      "coreClient" -> client.coreYetuClient,
      "redirectUris" -> client.redirectURIs,
      "grantTypes" -> client.grantTypes
    )

    WS.url(url("oauth2_clients", Version1)).post(clientJson).map(response => Unit)
  }

  override def findClient(clientId: String) = {

    logger.info("current url: " + urlForResource("oauth2_clients", clientId, Version1))
    WS.url(urlForResource("oauth2_clients", clientId, Version1)).get().map(response => {

      logger.info("current response status: " + response.status)
      if (response.status == Http.Status.OK) {

        val client = Json.parse(response.body)

        val clientId = (client \ "id").as[String]
        val clientSecret = (client \ "secret").as[String]
        val clientName = (client \ "name").as[String]
        val coreYetuClient = (client \ "coreClient").as[Boolean]

        val redirectUris = (client \ "redirectUris").asOpt[List[String]]
        val grantTypes = (client \ "grantTypes").asOpt[List[String]]

        Some(new OAuth2Client(
          clientId,
          clientSecret,
          redirectUris.getOrElse(List()),
          grantTypes,
          clientName,
          coreYetuClient
        ))

      } else None
    })
  }

  override def deleteClient(client: OAuth2Client) = deleteClient(client.clientId)

  override def deleteClient(clientId: String) = {
    WS.url(urlForResource("oauth2_clients", clientId, Version1)).delete().map(_ => Unit)
  }
}
