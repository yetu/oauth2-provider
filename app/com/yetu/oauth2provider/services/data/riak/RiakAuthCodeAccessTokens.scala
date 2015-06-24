package com.yetu.oauth2provider.services.data.riak

import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.IAuthCodeAccessTokenService
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.concurrent.Future
import scalaoauth2.provider.{ AccessToken, AuthInfo }

/**
 * riak implementation for authorization codes and access tokens given to OAuth2 clients such as the homescreen
 */
class RiakAuthCodeAccessTokens(riakConnection: RiakConnection) extends IAuthCodeAccessTokenService with NamedLogger {

  implicit val formatAccessToken = Json.format[AccessToken]

  implicit def formatAuthInfo[T: Format]: Format[AuthInfo[T]] =
    ((__ \ "user").format[T] and
      (__ \ "clientId").formatNullable[String] and
      (__ \ "scope").formatNullable[String] and
      (__ \ "redirectUri").formatNullable[String]
    )(AuthInfo.apply, unlift(AuthInfo.unapply))

  import scala.concurrent.ExecutionContext.Implicits.global

  def saveAccessToken(token: String, accessToken: AccessToken) = {
    logger.debug(s"saveAccessToken token: $token, accessToken: $accessToken")
    riakConnection.accessTokenBucket.store(token, Json.toJson(accessToken).toString())
  }

  def saveAuthCode(code: String, authInfo: AuthInfo[YetuUser]) = {
    logger.debug(s"saveAuthCode code: $code, authInfo: $authInfo")
    riakConnection.accessTokenBucket.store(code, Json.toJson(authInfo).toString())
  }

  def saveAccessTokenToAuthInfo(token: String, authInfo: AuthInfo[YetuUser]) = {
    logger.debug(s"saveAccessTokenToAuthInfo token: $token, authInfo: $authInfo")
    riakConnection.authInfoBucket.store(token, Json.toJson(authInfo).toString())
  }

  def saveAuthInfoToAccessToken(key: String, accessToken: AccessToken): Future[Unit] = {
    logger.debug(s"saveAuthInfoToAccessToken key: $key, accessToken: $accessToken")
    riakConnection.accessTokenBucket.store(key, Json.toJson(accessToken).toString())
  }

  def findAuthInfoByAuthCode(code: String) = {
    riakConnection.accessTokenBucket.fetch(code).map(p => {
      p.map(o => Json.parse(o.data).as[AuthInfo[YetuUser]])
    })
  }

  def findAuthInfoByAccessToken(token: String) = {
    riakConnection.authInfoBucket.fetch(token).map(p => {
      p.map(o => Json.parse(o.data).as[AuthInfo[YetuUser]])
    })
  }

  def findAccessTokenByAuthInfo(key: String): Future[Option[AccessToken]] = {
    riakConnection.accessTokenBucket.fetch(key).map(p => {
      p.map(o => Json.parse(o.data).as[AccessToken])
    })
  }

  def findAccessToken(token: String) = {
    logger.debug(s"findAccessToken token: $token")
    riakConnection.accessTokenBucket.fetch(token).map(p => {
      p.map(o => Json.parse(o.data).as[AccessToken])
    })
  }

  def deleteAuthCode(code: String): Future[Unit] = {
    riakConnection.authInfoBucket.delete(code)
  }
}
