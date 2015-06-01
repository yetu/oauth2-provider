package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.oauth2.models.Temp.AuthInformation
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import play.api.Logger

import scala.concurrent.Future
import scalaoauth2.provider.AccessToken

/**
 * riak implementation for authorization codes and access tokens given to OAuth2 clients such as the homescreen
 */
class RiakAuthCodeAccessTokens(riakConnection: RiakConnection) extends IAuthCodeAccessTokenService {

  val logger = Logger(this.getClass)

  def saveAuthCode(user: YetuUser, code: String) = {
    logger.debug(s"saveAuthCode code=$code user=$user")
  }

  def findUserByAuthCode(code: String) = {
    logger.debug(s"findUserByAuthCode code=$code")
    Future.successful(None)
  }

  def saveAccessToken(token: String, accessToken: AccessToken) = {

  }

  def findAuthCodeByUser(user: YetuUser) = {
    logger.debug(s"findAuthCodeByUser user=$user")

    Future.successful(None)
  }

  def findAccessTokenByUser(user: YetuUser) = {
    logger.debug(s"findAccessTokenByUser user=$user")

    Future.successful(None)
  }

  def findTokenByAccessToken(accessToken: AccessToken) = {
    logger.debug(s"findTokenByAccessToken accessToken=$accessToken")

    Future.successful(None)
  }

  def saveAuthCodeToAuthInfo(code: String, authInfo: AuthInformation) = {
    logger.debug(s"saveAuthCodeToAuthInfo code: $code, authInfo: $authInfo")
  }

  def findAuthInfoByAuthCode(code: String) = {
    Future.successful(None)
  }

  def saveAccessTokenToUser(accessToken: AccessToken, authInfo: AuthInformation) = {
    logger.debug(s"saveAccessTokenToUser accessToken: $accessToken")
    logger.debug(s"saveAccessTokenToUser authInfo: $authInfo")
  }

  def findUserByAccessToken(accessToken: AccessToken) = {
    Future.successful(None)
  }

  def findAccessToken(token: String) = {
    logger.debug(s"findAccessToken token: $token")
    Future.successful(None)
  }
}
