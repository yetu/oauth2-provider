package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.oauth2.models.YetuUser
import scalaoauth2.provider.AuthInfo
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import play.api.Logger
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import scalaoauth2.provider.AccessToken

/**
 * riak implementation for authorization codes and access tokens given to OAuth2 clients such as the homescreen
 */
class RiakAuthCodeAccessTokens(riakConnection: RiakConnection) extends IAuthCodeAccessTokenService {

  implicit val formatAccessToken = Json.format[AccessToken]

  implicit def formatAuthInfo[T: Format]: Format[AuthInfo[T]] =
    ((__ \ "user").format[T] and
      (__ \ "clientId").formatNullable[String] and
      (__ \ "scope").formatNullable[String] and
      (__ \ "redirectUri").formatNullable[String]
    )(AuthInfo.apply, unlift(AuthInfo.unapply))

  import scala.concurrent.ExecutionContext.Implicits.global
  val logger = Logger(this.getClass)

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
    riakConnection.authCodeBucket.store(token, Json.toJson(authInfo).toString())
  }

  def findAuthInfoByAuthCode(code: String) = {
    riakConnection.accessTokenBucket.fetch(code).map(p => {
      p.map(o => Json.parse(o.data).as[AuthInfo[YetuUser]])
    })
  }

  def findAuthInfoByAccessToken(token: String) = {
    riakConnection.authCodeBucket.fetch(token).map(p => {
      p.map(o => Json.parse(o.data).as[AuthInfo[YetuUser]])
    })
  }

  def findAccessToken(token: String) = {
    logger.debug(s"findAccessToken token: $token")
    riakConnection.accessTokenBucket.fetch(token).map(p => {
      p.map(o => Json.parse(o.data).as[AccessToken])
    })
  }

  def deleteAuthCode(code: String): Future[Unit] = {
    riakConnection.authCodeBucket.delete(code)
  }
}
