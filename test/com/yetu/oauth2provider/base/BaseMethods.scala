package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.IAuthCodeAccessTokenService
import com.yetu.oauth2provider.utils.Config
import play.api.Logger

trait BaseMethods extends DefaultTestVariables {

  def authCodeAccessTokenService: IAuthCodeAccessTokenService

  val logger = Logger("TEST")
  def log(s: String) = logger.warn(s)

  def generateAndSaveTestVariables(scope: String = Config.SCOPE_BASIC, user: YetuUser = testUser, clientId: String = testClientId, redirectURIs: List[String] = List(defaultRedirectUrl)) = {

    val (authInfo, token) = generateTestVariables(scope, user, clientId, redirectURIs)
    authCodeAccessTokenService.saveAccessToken(token, authInfo)

    (authInfo, token)
  }

}
