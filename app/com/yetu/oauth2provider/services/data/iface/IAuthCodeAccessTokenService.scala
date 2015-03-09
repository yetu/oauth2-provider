package com.yetu.oauth2provider.services.data.iface

import com.yetu.oauth2provider.oauth2.models.Temp._
import com.yetu.oauth2provider.oauth2.models.YetuUser

import scalaoauth2.provider.AccessToken

//TODO: there should be a fewer save/find methods
//TODO: instead nulab's AuthInfo and securesocial's Identity should be better linked together.

//TODO scopes need to be taken into account.
//TODO: this interface should be redesigned
trait IAuthCodeAccessTokenService {

  def saveAuthCode(user: YetuUser, code: String)

  def findUserByAuthCode(code: String): Option[YetuUser]

  def saveAccessToken(token: String, accessToken: AccessToken)

  def findAuthCodeByUser(identity: YetuUser): Option[String]

  def findAccessTokenByUser(identity: YetuUser): Option[AccessToken]

  def findTokenByAccessToken(accessToken: AccessToken): Option[String]

  def deleteAll(identity: YetuUser)

  def saveAuthCodeToAuthInfo(code: String, authInfo: AuthInformation)

  def findAuthInfoByAuthCode(code: String): Option[AuthInformation]

  def saveAccessTokenToUser(accessToken: AccessToken, authInfo: AuthInformation)

  def findUserByAccessToken(accessToken: AccessToken): Option[AuthInformation]

  def findAccessToken(token: String): Option[AccessToken]

}
