package com.yetu.oauth2provider.services.data.api

import com.yetu.oauth2provider.services.data.interface.IPublicKeyService
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.mvc.Http

import scala.concurrent.Future

import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

class APIPublicKeyService extends IPublicKeyService with APIHelper {

  override def storeKeyF(userId: String, key: YetuPublicKey): Future[Unit] = {

    val keyData = Json.obj(
      "userId" -> userId,
      "publicKey" -> key.key
    )

    WS.url(url("rsa_keys", Version1)).post(keyData).map(response => {
      Unit
    })
  }

  override def getKeyF(userId: String): Future[Option[YetuPublicKey]] = {

    WS.url(urlForResource("rsa_keys", userId, Version1)).get().map(response => {
      if (response.status == Http.Status.OK) {

        val key = Json.parse(response.body)
        Some(YetuPublicKey((key \ "publicKey").as[String]))

      } else None
    })
  }
}