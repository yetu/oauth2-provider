package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.YetuUser

import scalaoauth2.provider.AuthInfo
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import play.api.Logger

import scala.concurrent.Future
import scalaoauth2.provider.AccessToken

/**
 * in-memory implementation for authorization codes and access tokens given to OAuth2 clients such as the homescreen
 */
class MemoryAuthCodeAccessTokens extends IAuthCodeAccessTokenService {

  val logger = Logger(this.getClass)
  import com.yetu.oauth2provider.services.data.MemoryAuthCodeAccessTokens._

  def saveAccessToken(token: String, accessToken: AccessToken) = {
    logger.debug(s"saveAuthCode token=$token accessToken=$accessToken")
    Future.successful(accessTokens += (token -> accessToken))
  }

  def saveAuthCode(code: String, authInfo: AuthInfo[YetuUser]) = {
    logger.debug(s"saveAuthCode code=$code authInfo=$authInfo")
    Future.successful(authCodes += (code -> authInfo))
  }

  def saveAccessTokenToAuthInfo(token: String, authInfo: AuthInfo[YetuUser]) = {
    logger.debug(s"saveAuthCode token=$token authInfo=$authInfo")
    Future.successful(accessTokensToAuthInfo += (token -> authInfo))
  }

  def saveAuthInfoToAccessToken(key: String, accessToken: AccessToken): Future[Unit] = {
    logger.debug(s"saveAuthInfoToAccessToken key: $key, accessToken: $accessToken")
    Future.successful(accessTokens += (key -> accessToken))
  }

  def findAuthInfoByAuthCode(code: String): Future[Option[AuthInfo[YetuUser]]] = {
    val info = authCodes.get(code)
    logger.debug(s"findAuthInfoByAuthCode code: $code result=$info")
    Future.successful(info)
  }

  def findAuthInfoByAccessToken(token: String) = {
    val authInfo: Option[AuthInfo[YetuUser]] = accessTokensToAuthInfo.get(token)
    logger.debug(s"findUserByAccessToken token: $token, authInfo: $authInfo")
    Future.successful(authInfo)
  }

  def findAccessTokenByAuthInfo(key: String): Future[Option[AccessToken]] = {
    val accessToken: Option[AccessToken] = accessTokens.get(key)
    logger.debug(s"findUserByAccessToken token: $accessToken")
    Future.successful(accessToken)
  }

  def findAccessToken(token: String) = {
    logger.debug(s"findAccessToken token: $token")
    Future.successful(accessTokens.get(token))
  }

  def deleteAuthCode(code: String): Future[Unit] = {
    Future.successful(authCodes -= code)
  }
}

object MemoryAuthCodeAccessTokens {
  /*
   * stores authentication codes sent in the parameters when redirecting to the OAuth client
   * e.g. redirect to homescreen.com?code=AUTH_CODE
   */

  var accessTokens = Map[String, AccessToken]()
  var authCodes = Map[String, AuthInfo[YetuUser]]()
  var accessTokensToAuthInfo = Map[String, AuthInfo[YetuUser]]()
}

