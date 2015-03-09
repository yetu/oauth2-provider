package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.services.data.iface.IPublicKeyService
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import play.api.Logger

import scala.concurrent.Future

class LdapPublicKeyService(personService: LdapPersonService) extends IPublicKeyService {

  lazy val logger = Logger("com.yetu.oauth2provider.services.data.LdapPublicKeyService ")

  override def storeKey(userId: String, key: YetuPublicKey): Unit = {
    personService.modifyUserPublicKey(userId, key)
  }

  override def getKey(userId: String): Option[YetuPublicKey] = {
    logger.debug(s"LDAP GET KEY: userId=$userId")
    personService.findYetuUser(userId).flatMap(_.publicKey)
  }

  override def storeKeyF(userId: String, key: YetuPublicKey): Future[Unit] = {
    Future.successful(personService.modifyUserPublicKey(userId, key))
  }

  override def getKeyF(userId: String): Future[Option[YetuPublicKey]] = {
    Future.successful{
      personService.findYetuUser(userId)
        .flatMap(_.publicKey)
    }
  }
}
