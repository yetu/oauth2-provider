package com.yetu.oauth2provider.services.data.memory

import com.yetu.oauth2provider.services.data.interface.IPublicKeyService
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import play.api.Logger

import scala.concurrent.Future

class MemoryPublicKeyService extends IPublicKeyService {
  import MemoryPublicKeyService.keys

  lazy val logger = Logger("com.yetu.oauth2provider.services.data.memory.MemoryPublicKeyService ")

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
