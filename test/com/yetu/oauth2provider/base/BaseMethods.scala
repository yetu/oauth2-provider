package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.utils.Config
import play.api.Logger

trait BaseMethods extends DefaultTestVariables with TestRegistry {
  val logger = Logger("TEST")
  def log(s: String) = logger.warn(s)

  def generateAndSaveTestVariables(scope: String = Config.SCOPE_BASIC, user: YetuUser = testUser, clientId: String = testClientId, redirectURIs: List[String] = List(defaultRedirectUrl)) = {

    val (authInfo, token) = generateTestVariables(scope, user, clientId, redirectURIs)

    authCodeAccessTokenService.saveAccessToken(token.token, token)
    authCodeAccessTokenService.saveAccessTokenToUser(token, authInfo)
    (authInfo, token)

  }

}
