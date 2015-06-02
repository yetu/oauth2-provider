package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.Temp.AuthInformation
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import play.api.Logger

import scala.concurrent.Future
import scalaoauth2.provider.AccessToken

/**
 * in-memory implementation for authorization codes and access tokens given to OAuth2 clients such as the homescreen
 */
class MemoryAuthCodeAccessTokens extends IAuthCodeAccessTokenService {

  val logger = Logger(this.getClass())
  import com.yetu.oauth2provider.services.data.MemoryAuthCodeAccessTokens._

  //TODO: there should be a fewer save/find methods
  //TODO: instead nulab's AuthInfo and securesocial's Identity should be better linked together.

  //TODO scopes need to be taken into account.

  def saveAuthCode(user: YetuUser, code: String) = {
    logger.debug(s"saveAuthCode code=$code user=$user")
    Future.successful(authCodes += (code -> user))
  }

  def findUserByAuthCode(code: String) = {
    logger.debug(s"findUserByAuthCode code=$code")
    Future.successful(authCodes.get(code))
  }

  def saveAccessToken(token: String, accessToken: AccessToken) = {
    Future.successful(accessTokens += (token -> accessToken))
  }

  def findAuthCodeByUser(user: YetuUser) = {
    logger.debug(s"findAuthCodeByUser user=$user")
    Future.successful(authCodes.find(_._2.equals(user)).map(_._1))
  }

  def findAccessTokenByUser(user: YetuUser) = {
    logger.debug(s"findAccessTokenByUser user=$user")
    Future.successful(accessTokensWithUser
      .find(_._2.user.identityId.userId == user.identityId.userId)
      .map(_._1))
  }

  def findTokenByAccessToken(accessToken: AccessToken) = {
    logger.debug(s"findTokenByAccessToken accessToken=$accessToken")
    Future.successful(accessTokens.find(_._2.equals(accessToken)).map(_._1))
  }

  def saveAuthCodeToAuthInfo(code: String, authInfo: AuthInformation) = {
    logger.debug(s"saveAuthCodeToAuthInfo code: $code, authInfo: $authInfo")
    authCodeAuthInfo += (code -> authInfo)
  }

  def findAuthInfoByAuthCode(code: String) = {
    val info = authCodeAuthInfo.get(code)
    logger.debug(s"findAuthInfoByAuthCode code: $code result=$info")
    Future.successful(info)
  }

  def saveAccessTokenToUser(accessToken: AccessToken, authInfo: AuthInformation) = {
    logger.debug(s"saveAccessTokenToUser accessToken: $accessToken")
    logger.debug(s"saveAccessTokenToUser authInfo: $authInfo")
    accessTokensWithUser += (accessToken -> authInfo)
  }

  def findUserByAccessToken(accessToken: AccessToken) = {
    val authInfo: Option[AuthInformation] = accessTokensWithUser.get(accessToken)
    logger.debug(s"findUserByAccessToken accessToken: $accessToken")
    logger.debug(s"findUserByAccessToken authInfo: $authInfo")
    Future.successful(authInfo)
  }

  def findAccessToken(token: String) = {
    logger.debug(s"findAccessToken token: $token")
    Future.successful(accessTokens.get(token))
  }

}

object MemoryAuthCodeAccessTokens {
  /*
   * stores authentication codes sent in the parameters when redirecting to the OAuth client
   * e.g. redirect to homescreen.com?code=AUTH_CODE
   */
  var authCodes = Map[String, YetuUser]()
  var authCodeAuthInfo = Map[String, AuthInformation]()

  var accessTokens = Map[String, AccessToken]()
  var accessTokensWithUser = Map[AccessToken, AuthInformation]()
}

