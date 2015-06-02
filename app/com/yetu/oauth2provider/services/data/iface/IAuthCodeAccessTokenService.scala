package com.yetu.oauth2provider.services.data.iface

import com.yetu.oauth2provider.oauth2.models.YetuUser

import scala.concurrent.Future
import scalaoauth2.provider.{ AuthInfo, AccessToken }

trait IAuthCodeAccessTokenService {

  import scala.concurrent.ExecutionContext.Implicits.global

  def saveAccessToken(token: String, accessToken: AccessToken): Future[Unit]

  def saveAccessToken(accessToken: AccessToken, authInfo: AuthInfo[YetuUser]): Future[Unit] = {
    val saveToken = saveAccessToken(accessToken.token, accessToken)
    val saveAuthInfo = saveAccessTokenToAuthInfo(accessToken.token, authInfo)
    for {
      token <- saveToken
      info <- saveAuthInfo
    } yield info
  }

  def saveAuthCode(code: String, authInfo: AuthInfo[YetuUser]): Future[Unit]

  def saveAccessTokenToAuthInfo(token: String, authInfo: AuthInfo[YetuUser]): Future[Unit]

  def findAuthInfoByAuthCode(code: String): Future[Option[AuthInfo[YetuUser]]]

  def findAuthInfoByAccessToken(token: String): Future[Option[AuthInfo[YetuUser]]]

  def findAccessToken(token: String): Future[Option[AccessToken]]

  def deleteAuthCode(code: String): Future[Unit]

}
