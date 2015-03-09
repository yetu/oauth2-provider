package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.services.data.iface.IPublicKeyService
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import play.api.Logger

import scala.concurrent.Future

class MemoryPublicKeyService extends IPublicKeyService {
  import MemoryPublicKeyService.keys

  lazy val logger = Logger("com.yetu.oauth2provider.services.data.MemoryPublicKeyService ")

  override def storeKey(userId: String, key: YetuPublicKey): Unit = {
    keys += userId -> key
  }

  override def getKey(userId: String): Option[YetuPublicKey] = {

    logger.warn(s" GET KEY: userId=$userId, keys= $keys")
    keys.find(_._1 == userId).map(_._2)
  }

  override def storeKeyF(userId: String, key: YetuPublicKey): Future[Unit] = {
    Future.successful(keys += userId -> key)
  }

  override def getKeyF(userId: String): Future[Option[YetuPublicKey]] = {
    Future.successful{
      keys.find(_._1 == userId).map(_._2)

    }
  }
}

object MemoryPublicKeyService {

  var keys = Map[String, YetuPublicKey]()
}
