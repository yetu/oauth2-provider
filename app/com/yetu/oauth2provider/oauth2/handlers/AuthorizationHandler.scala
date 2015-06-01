package com.yetu.oauth2provider.oauth2.handlers

import java.util.Date

import com.yetu.oauth2provider.oauth2.models.Temp.AuthInformation
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.{ IAuthCodeAccessTokenService, IClientService, IPersonService }
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
    //TODO: validate clientId String length and allowed symbols?
    logger.debug("validating client ...")

    val validationResult = clientService.findClient(clientCredential.clientId) match {
      case None => {
        logger.debug(s"No client with this id: ${clientCredential.clientId}")
        false
      }
      case Some(oauthClient) => {

        logger.debug("given:")
        logger.debug(s"id = ${clientCredential.clientId} , secret = ${clientCredential.clientSecret}, grantType = $grantType")
        logger.debug("expected:")
        logger.debug(s"id = ${oauthClient.clientId} , secret = ${oauthClient.clientSecret}, grantType = ${oauthClient.grantTypes}")

        val validClientId = oauthClient.clientId == clientCredential.clientId
        val validGrantType = oauthClient.grantTypes.exists(grantList => grantList.contains(grantType))
        val validSecret = clientCredential.clientSecret.contains(oauthClient.clientSecret)

        if (grantType == Config.GRANT_TYPE_TOKEN) {
          validClientId && validGrantType
        } else {
          validClientId && validGrantType && validSecret
        }

      }
    }
    logger.debug(s"...validating client ${clientCredential.clientId}: $validationResult")
    Future.successful(validationResult)
  }

  def createAccessToken(authInfo: AuthInformation) = {

    val refreshToken = BearerTokenGenerator.generateToken
    val jsonWebToken = jsonWebTokenGenerator.generateToken(authInfo)
    val token = new AccessToken(jsonWebToken,
      Some(refreshToken),
      authInfo.scope,
      Some(Config.OAuth2.accessTokenExpirationInSeconds.toLong),
      new Date(System.currentTimeMillis()))

    authAccessService.saveAccessToken(jsonWebToken, token)
    authAccessService.saveAccessTokenToUser(token, authInfo)
    logger.debug(s"...create access Token: $token")
    Future.successful(token)
  }

  def findAuthInfoByCode(code: String): Future[Option[AuthInformation]] = {
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
    authAccessService.findUserByAccessToken(accessToken)
  }

  def findUser(username: String, password: String): Future[Option[YetuUser]] = {
    logger.warn("Find user... ")

    val loggedIn = for {
      user <- personService.findYetuUser(username)
      pinfo <- user.passwordInfo
      hasher <- passwordHashers.get(pinfo.hasher) if hasher.matches(pinfo, password)
    } yield user

    logger.debug(s"user found? user=$loggedIn")

    Future.successful(loggedIn)
  }

  def getStoredAccessToken(authInfo: AuthInformation) = {
    logger.warn("...getStoredAccessToken :: NOT_IMPLEMENTED")
    Future.successful(None)
  }

  def refreshAccessToken(authInfo: AuthInformation, refreshToken: String) = {

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

  override def deleteAuthCode(code: String): Future[Unit] = ???
}

//*   <li>validateClient(clientId, clientSecret, grantType)</li>
//*   <li>findAuthInfoByCode(code)</li>
//*   <li>getStoredAccessToken(authInfo)</li>
//*   <li>isAccessTokenExpired(token)</li>
//*   <li>refreshAccessToken(authInfo, token)
//*   <li>createAccessToken(authInfo)</li>

//<li>validateClient(clientId, clientSecret, grantType)</li>
//*   X<li>findAuthInfoByCode(code)</li>
//*   X<li>getStoredAccessToken(authInfo)</li>
//*   not needed? <li>refreshAccessToken(authInfo, token)
//*   X<li>createAccessToken(authInfo)</li>

//*   <li>findAccessToken(token)</li>
//*   <li>findAuthInfoByAccessToken(token)</li>
