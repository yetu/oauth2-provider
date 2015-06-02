package com.yetu.oauth2provider.services.data.iface

import com.yetu.oauth2provider.oauth2.models.Temp._
import com.yetu.oauth2provider.oauth2.models.YetuUser

import scala.concurrent.Future
import scalaoauth2.provider.AccessToken

//TODO: there should be a fewer save/find methods
//TODO: instead nulab's AuthInfo and securesocial's Identity should be better linked together.

//TODO scopes need to be taken into account.
//TODO: this interface should be redesigned
trait IAuthCodeAccessTokenService {

  def saveAuthCode(user: YetuUser, code: String): Future[Unit]

  def findUserByAuthCode(code: String): Future[Option[YetuUser]]

  def saveAccessToken(token: String, accessToken: AccessToken): Future[Unit]

  def findAuthCodeByUser(identity: YetuUser): Future[Option[String]]

  def findAccessTokenByUser(identity: YetuUser): Future[Option[AccessToken]]

  def findTokenByAccessToken(accessToken: AccessToken): Future[Option[String]]

  def saveAuthCodeToAuthInfo(code: String, authInfo: AuthInformation)

  def findAuthInfoByAuthCode(code: String): Future[Option[AuthInformation]]

  def saveAccessTokenToUser(accessToken: AccessToken, authInfo: AuthInformation)

  def findUserByAccessToken(accessToken: AccessToken): Future[Option[AuthInformation]]

  def findAccessToken(token: String): Future[Option[AccessToken]]

}
