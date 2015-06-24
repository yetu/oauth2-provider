package com.yetu.oauth2provider.services.data.interface

import com.yetu.oauth2provider.signature.models.YetuPublicKey

import scala.concurrent.Future

trait IPublicKeyService {

  @deprecated("Use the async Future[] returning method instead.", "11-11-2014")
  def storeKey(userId: String, key: YetuPublicKey): Unit

  @deprecated("Use the async Future[] returning method instead.", "11-11-2014")
  def getKey(userId: String): Option[YetuPublicKey]

  def storeKeyF(userId: String, key: YetuPublicKey): Future[Unit]

  def getKeyF(userId: String): Future[Option[YetuPublicKey]]

}
