package com.yetu.oauth2provider.oauth2.handlers

import java.util.Date

import scalaoauth2.provider.AuthInfo
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.{ IAuthCodeAccessTokenService, IClientService, IPersonService }
import com.yetu.oauth2provider.utils.{ Config, JsonWebTokenGenerator, BearerTokenGenerator }
import play.api.Logger
import securesocial.core.providers.utils.PasswordHasher

import scala.concurrent.Future
import scalaoauth2.provider.{ AccessToken, ClientCredential, DataHandler }

class AuthorizationHandler(authAccessService: IAuthCodeAccessTokenService,
    clientService: IClientService,
    personService: IPersonService,
    passwordHashers: Map[String, PasswordHasher],
    jsonWebTokenGenerator: JsonWebTokenGenerator) extends DataHandler[YetuUser] {

  import scala.concurrent.ExecutionContext.Implicits.global
  val logger = Logger(this.getClass)

  override def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] = {
    logger.debug("validating client ...")

    clientService.findClient(clientCredential.clientId).map {
      case None =>
        logger.debug(s"No client with this id: ${clientCredential.clientId}")
        false

      case Some(oauthClient) =>

        logger.debug("given:")
        logger.debug(s"id = ${clientCredential.clientId} , secret = ${clientCredential.clientSecret}, grantType = $grantType")
        logger.debug("expected:")
        logger.debug(s"id = ${oauthClient.clientId} , secret = ${oauthClient.clientSecret}, grantType = ${oauthClient.grantTypes}")

        val validClientId = oauthClient.clientId == clientCredential.clientId
        val validGrantType = oauthClient.grantTypes.exists(grantList => grantList.contains(grantType))
        val validSecret = clientCredential.clientSecret.map(_ == oauthClient.clientSecret).getOrElse(false)

        if (grantType == Config.GRANT_TYPE_TOKEN) {
          validClientId && validGrantType
        } else {
          validClientId && validGrantType && validSecret
        }
    }
  }

  def createAccessToken(authInfo: AuthInfo[YetuUser]) = {

    val refreshToken = BearerTokenGenerator.generateToken
    val jsonWebToken = jsonWebTokenGenerator.generateToken(authInfo)
    val token = new AccessToken(jsonWebToken,
      Some(refreshToken),
      authInfo.scope,
      Some(Config.OAuth2.accessTokenExpirationInSeconds.toLong),
      new Date(System.currentTimeMillis()))

    authAccessService.saveAccessToken(token, authInfo)

    logger.debug(s"...create access Token: $token")
    Future.successful(token)
  }

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[YetuUser]]] = {
    for {
      authInfo <- authAccessService.findAuthInfoByAuthCode(code)
      log = logger.debug(s"findAuthInfoByCode: $code -> authInfo: $authInfo")
    } yield authInfo
  }

  def findAccessToken(token: String) = {
    logger.debug(s"findAccessToken: $token")
    authAccessService.findAccessToken(token)
  }

  def findAuthInfoByAccessToken(accessToken: AccessToken) = {
    logger.debug(s"findAuthInfoByAccessToken: $accessToken")
    authAccessService.findAuthInfoByAccessToken(accessToken.token)
  }

  def findUser(username: String, password: String): Future[Option[YetuUser]] = {
    personService.findYetuUser(username).map {
      case Some(user) =>

        val itMatches = user.passwordInfo
          .flatMap(pInfo => passwordHashers.get(pInfo.hasher)
            .map(_.matches(pInfo, password)))
          .getOrElse(false)

        if (itMatches) {
          Some(user)
        } else None

      case _ => None
    }
  }

  def getStoredAccessToken(authInfo: AuthInfo[YetuUser]) = {
    authInfo.clientId match {
      case Some(clientId) =>
        authAccessService.findAccessTokenByAuthInfo(clientId + authInfo.user.userId)
      case _ => Future.successful(None)
    }
  }

  def refreshAccessToken(authInfo: AuthInfo[YetuUser], refreshToken: String) = {
    logger.warn("...refreshAccessToken :: NOT_IMPLEMENTED")
    createAccessToken(authInfo)
  }

  def findAuthInfoByRefreshToken(refreshToken: String) = {
    logger.warn("...findAuthInfoByRefreshToken :: NOT_IMPLEMENTED")
    Future.successful(None)
  }

  override def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[YetuUser]] = {
    logger.warn("...findClientUser :: NOT_IMPLEMENTED")
    Future.successful(None)
  }

  override def deleteAuthCode(code: String): Future[Unit] = {
    authAccessService.deleteAuthCode(code)
  }
}
