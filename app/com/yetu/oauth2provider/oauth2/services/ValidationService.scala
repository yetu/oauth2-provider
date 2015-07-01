package com.yetu.oauth2provider.oauth2.services

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.{ Config, DateUtility }
import com.yetu.oauth2resource.model.ValidationResponse
import play.api.libs.json.{ JsValue, Json }

import scalaoauth2.provider.AuthInfo

class ValidationService {

  def generateJsonResponse(authInfo: AuthInfo[YetuUser]): JsValue = Json.toJson(generateResponse(authInfo))

  def generateResponse(authInfo: AuthInfo[YetuUser]): ValidationResponse = {

    ValidationResponse(
      userUUID = Some(authInfo.user.userId),
      clientId = authInfo.clientId,
      iat = Some(DateUtility.unixSecondsNow()),
      exp = Some(DateUtility.unixSecondsDefaultExpiration()),
      sub = Some(authInfo.user.userId),
      iss = Some(Config.publicUrl),
      aud = authInfo.scope)
  }

  def generateJsonResponseDeprecated(authInfo: AuthInfo[YetuUser]): JsValue = Json.toJson(generateResponseDeprecated(authInfo))

  def generateResponseDeprecated(authInfo: AuthInfo[YetuUser]): ValidationResponse = {
    ValidationResponse(userUUID = Some(authInfo.user.userId),
      scope = authInfo.scope,
      userId = Some(authInfo.user.userId),
      clientId = authInfo.clientId,
      userEmail = authInfo.user.email,
      iat = Some(DateUtility.unixSecondsNow()),
      exp = Some(DateUtility.unixSecondsDefaultExpiration()),
      sub = Some(authInfo.user.userId),
      iss = Some(Config.publicUrl),
      aud = authInfo.scope
    )
  }

}
