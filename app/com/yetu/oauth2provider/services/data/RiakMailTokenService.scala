package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.services.data.iface.IMailTokenService
import play.api.Logger
import securesocial.core.providers.MailToken

import scala.concurrent.Future

/**
 * A token used for reset password and sign up operations, i.e., the UUID used in the URL for signup sent via email,
 * e.g. /signup/fiusdagbakf0a-fzsfb-zfbzbbs
 *
 * validity can be configured in securesocial.conf, default = 60 minutes
 */
class RiakMailTokenService extends IMailTokenService {

  val tokenLogger = Logger("com.yetu.oauth2provider.services.data.RiakMailTokenService")

  def saveToken(token: MailToken): Future[MailToken] = {
    Future.successful(token)
  }

  def findToken(token: String): Future[Option[MailToken]] = {
    Future.successful(None)
  }

  def deleteToken(uuid: String): Future[Option[MailToken]] = {
    Future.successful(None)
  }

  def deleteTokens() {

  }

  def deleteExpiredTokens() {

  }
}