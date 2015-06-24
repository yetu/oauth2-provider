package com.yetu.oauth2provider.services.data.interface

import com.yetu.oauth2provider.signature.models.YetuPublicKey

import scala.concurrent.Future

trait IPublicKeyService {

  def storeKeyF(userId: String, key: YetuPublicKey): Future[Unit]

  def getKeyF(userId: String): Future[Option[YetuPublicKey]]

}
