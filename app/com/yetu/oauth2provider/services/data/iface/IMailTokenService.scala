package com.yetu.oauth2provider.services.data.iface

import securesocial.core.providers.MailToken

import scala.concurrent.Future

/**
 * A token used for reset password and sign up operations, i.e., the UUID used in the URL for signup sent via email,
 * e.g. /signup/fiusdagbakf0a-fzsfb-zfbzbbs
 *
 * validity can be configured in securesocial.conf, default = 60 minutes
 */
trait IMailTokenService {

  def saveToken(token: MailToken): Future[MailToken]

  def findToken(token: String): Future[Option[MailToken]]

  def deleteToken(uuid: String): Future[Option[MailToken]]

  def deleteExpiredTokens()

}
