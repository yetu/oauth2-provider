package com.yetu.oauth2provider.services.data.memory

import com.yetu.oauth2provider.services.data.interface.IMailTokenService
import play.api.Logger
import securesocial.core.providers.MailToken

import scala.concurrent.Future

/**
 * A token used for reset password and sign up operations, i.e., the UUID used in the URL for signup sent via email,
 * e.g. /signup/fiusdagbakf0a-fzsfb-zfbzbbs
 *
 * validity can be configured in securesocial.conf, default = 60 minutes
 */
class MemoryMailTokenService extends IMailTokenService {

  val tokenLogger = Logger("com.yetu.oauth2provider.services.data.MemoryMailTokenService")

  import MemoryMailTokenService.mailTokens

  def saveToken(token: MailToken): Future[MailToken] = {
    Future.successful {
      tokenLogger.info(s"New signup link or reset password requested. Check /signup/TOKEN or /reset/TOKEN with token=${token.uuid}")
      mailTokens += (token.uuid -> token)
      token
    }
  }

  def findToken(token: String): Future[Option[MailToken]] = {
    Future.successful { mailTokens.get(token) }
  }

  def deleteToken(uuid: String): Future[Option[MailToken]] = {
    Future.successful {
      mailTokens.get(uuid) match {
        case Some(token) =>
          mailTokens -= uuid
          Some(token)
        case None => None
      }
    }
  }

  def deleteTokens() {
    mailTokens = Map()
  }

  def deleteExpiredTokens() {
    mailTokens = mailTokens.filter(!_._2.isExpired)
  }
}

object MemoryMailTokenService {

  var mailTokens = Map[String, MailToken]()
}