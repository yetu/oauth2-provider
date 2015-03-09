package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.Temp.AuthInformation
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import play.api.Logger

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
    authCodes += (code -> user)
  }

  def findUserByAuthCode(code: String): Option[YetuUser] = {

    logger.debug(s"findUserByAuthCode code=$code")
    authCodes.get(code)
  }

  def saveAccessToken(token: String, accessToken: AccessToken) = {
    accessTokens += (token -> accessToken)
  }

  def findAuthCodeByUser(user: YetuUser): Option[String] = {
    logger.debug(s"findAuthCodeByUser user=$user")
    authCodes.foreach {
      case (key, value) => {
        if (value.equals(user)) return Some(key)
      }
    }
    None
  }

  def findAccessTokenByUser(user: YetuUser): Option[AccessToken] = {

    logger.debug(s"findAccessTokenByUser user=$user")
    accessTokensWithUser.foreach {
      case (key, value) => {
        logger.debug(value.user.identityId.userId + " - " + user.identityId)
        if (value.user.identityId.userId == user.identityId.userId) {
          return Some(key)
        }
      }
    }
    None
  }

  def findTokenByAccessToken(accessToken: AccessToken): Option[String] = {
    logger.debug(s"findTokenByAccessToken accessToken=$accessToken")
    accessTokens.foreach {
      case (key, value) => {
        if (value.equals(accessToken)) {
          return Some(key)
        }
      }
    }
    None
  }

  def deleteAll(identity: YetuUser) {
    try {
      val authCode = findAuthCodeByUser(identity)
      authCodes --= authCode
    } catch {
      case e: Exception => logger.debug("exception caught: " + e);
    }

    try {
      val accessToken = findAccessTokenByUser(identity)
      accessToken match {
        case None => None
        case Some(a) => {
          val token = findTokenByAccessToken(a)
          accessTokens --= token
        }
      }
      accessTokensWithUser --= accessToken
    } catch {
      case e: Exception => logger.debug("exception caught: " + e);
    }
  }

  def saveAuthCodeToAuthInfo(code: String, authInfo: AuthInformation) = {
    logger.debug(s"saveAuthCodeToAuthInfo code: $code, authInfo: $authInfo")
    authCodeAuthInfo += (code -> authInfo)
  }

  def findAuthInfoByAuthCode(code: String): Option[AuthInformation] = {
    val info = authCodeAuthInfo.get(code)
    logger.debug(s"findAuthInfoByAuthCode code: $code result=$info")
    info
  }

  def saveAccessTokenToUser(accessToken: AccessToken, authInfo: AuthInformation) = {
    logger.debug(s"saveAccessTokenToUser accessToken: $accessToken")
    logger.debug(s"saveAccessTokenToUser authInfo: $authInfo")
    accessTokensWithUser += (accessToken -> authInfo)
  }

  def findUserByAccessToken(accessToken: AccessToken): Option[AuthInformation] = {
    val authInfo: Option[AuthInformation] = accessTokensWithUser.get(accessToken)
    logger.debug(s"findUserByAccessToken accessToken: $accessToken")
    logger.debug(s"findUserByAccessToken authInfo: $authInfo")
    authInfo
  }

  def findAccessToken(token: String): Option[AccessToken] = {
    logger.debug(s"findAccessToken token: $token")
    accessTokens.get(token)
  }

}

object MemoryAuthCodeAccessTokens {
  /*
   * stores authentication codes sent in the parameters when redirecting to the OAuth client
   * e.g. redirect to homescreen.com?code=AUTH_CODE
   */
  var authCodes = Map[String, YetuUser]()

  var accessTokens = Map[String, AccessToken]()
  var accessTokensWithUser = Map[AccessToken, AuthInformation]()
  var authCodeAuthInfo = Map[String, AuthInformation]()
}

