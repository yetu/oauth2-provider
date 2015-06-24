package com.yetu.oauth2provider.services.data.riak

import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.services.data.interface.IMailTokenService
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.libs.json.Json
import securesocial.controllers.{ RegistrationInfo, UserAgreement }
import securesocial.core.providers.MailToken

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * A token used for reset password and sign up operations, i.e., the UUID used in the URL for signup sent via email,
 * e.g. /signup/fiusdagbakf0a-fzsfb-zfbzbbs
 *
 * validity can be configured in securesocial.conf, default = 60 minutes
 */
class RiakMailTokenService(riakConnection: RiakConnection) extends IMailTokenService with NamedLogger {

  implicit val formatUserAgreement = Json.format[UserAgreement]
  implicit val formatRegistrationInfo = Json.format[RegistrationInfo]
  implicit val formatMailToken = Json.format[MailToken]

  def saveToken(token: MailToken): Future[MailToken] = {
    riakConnection.mailTokenBucket
      .storeAndFetch(token.uuid, Json.toJson(token).toString())
      .map(result => Json.parse(result.data).as[MailToken])
  }

  def findToken(token: String): Future[Option[MailToken]] = {
    riakConnection.mailTokenBucket.fetch(token).map(p => {
      p.map(o => Json.parse(o.data).as[MailToken])
    })
  }

  def deleteToken(uuid: String): Future[Option[MailToken]] = {
    for {
      fetch <- findToken(uuid)
      delete <- riakConnection.mailTokenBucket.delete(uuid)
    } yield fetch
  }

  def deleteExpiredTokens() {
    // TODO index riak
    // research: does riak store some meta information including last updated?
    // if so, can we search for all mail tokens that have not expired yet?
    // if so, implement this and delete the old tokens.
    ???
  }
}