package com.yetu.oauth2provider.services.data.ldap

import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.IMailTokenService
import securesocial.core.providers.MailToken
import securesocial.core.services.UserService

import scala.concurrent.Future

/**
 * combines a PersonService with a MailTokenService to adhere to securesocial's UserService interface.
 */
class LdapUserService(dao: LdapDAO, mailTokenService: IMailTokenService) extends LdapPersonService(dao) with UserService[YetuUser] {

  override def saveToken(token: MailToken): Future[MailToken] = {
    mailTokenService.saveToken(token)
  }

  override def deleteToken(uuid: String): Future[Option[MailToken]] = {
    mailTokenService.deleteToken(uuid)
  }

  override def findToken(token: String): Future[Option[MailToken]] = {
    mailTokenService.findToken(token)
  }

  override def deleteExpiredTokens(): Unit = {
    mailTokenService.deleteExpiredTokens()
  }
}
