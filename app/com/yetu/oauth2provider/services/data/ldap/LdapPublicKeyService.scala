package com.yetu.oauth2provider.services.data.ldap

import com.yetu.oauth2provider.services.data.interface.IPublicKeyService
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class LdapPublicKeyService(personService: LdapPersonService) extends IPublicKeyService {

  lazy val logger = Logger("com.yetu.oauth2provider.services.data.ldap.LdapPublicKeyService ")

  override def storeKeyF(userId: String, key: YetuPublicKey): Future[Unit] = {
    Future.successful(personService.modifyUserPublicKey(userId, key))
  }

  override def getKeyF(userId: String): Future[Option[YetuPublicKey]] = {
    personService.findUser(userId).map(_.flatMap(_.publicKey))
  }
}
