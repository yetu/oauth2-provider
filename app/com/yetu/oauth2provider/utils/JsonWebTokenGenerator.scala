package com.yetu.oauth2provider.utils

import java.nio.file.{ Files, Paths }

import com.plasmaconduit.jwa.JWTRSA512
import com.plasmaconduit.jwt.JSONWebToken
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.oauth2.services.ValidationService
import play.api.libs.json._

import scalaoauth2.provider.AuthInfo

class JsonWebTokenGenerator(validationService: ValidationService) {

  def generateToken(authInfo: AuthInfo[YetuUser]): String = {

    val playJson = validationService.generateJsonResponse(authInfo)
    val payload = JsonUtility.convertPlayJsonToPlasmaJson(Json.toJson(playJson))

    val privateKey: Array[Byte] = Files.readAllBytes(Paths.get(Config.OAuth2.jsonWebTokenPrivateKeyFilename))

    JSONWebToken.sign(JWTRSA512, privateKey, payload)

  }

}

