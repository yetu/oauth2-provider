
package com.yetu.oauth2provider.utils

import java.nio.file.{ Paths, Files }

import com.plasmaconduit.json
import com.plasmaconduit.jws.JSONWebSignature
import com.plasmaconduit.jwt.JSONWebToken
import com.yetu.oauth2provider.base.{ DefaultTestVariables, BaseSpec, BaseRoutesSpec }
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2resource.model.ValidationResponse
import play.api.libs.json._

import org.scalatestplus.play._
import scala.util.Try
import scalaoauth2.provider.AuthInfo
import com.yetu.oauth2provider.utils.JsonUtility._
import scala.util.{ Failure, Success }
import play.api.test.Helpers._

class JsonWebTokenGeneratorSpec extends BaseRoutesSpec with DefaultTestVariables {

  lazy val publicKey: Array[Byte] = Files.readAllBytes(Paths.get(Config.OAuth2.jsonWebTokenPublicKeyFilename))

  "The JsonWebTokenGenerator" when {
    "receives a request for generating an JSON web token request" must {
      "provide an encoded JSON web token with uid, email and reserved json web token fields" in {
        val jsonWebToken = jsonWebTokenGenerator.generateToken(testUserInfo)
        val value = JSONWebToken.verify(publicKey, jsonWebToken).get // throws exception if verification fails
        value.toString must include (testUser.userId)
        value.toString must include ("userUUID")
        value.toString must include ("clientId")
        value.toString must include ("aud")
        value.toString must include ("exp")
        value.toString must include ("iat")
        value.toString must include ("iss")
        checkReservedFields(value)
      }
    }
  }

  def checkReservedFields(payload: json.JsValue): Unit = {

    val convertedPayload = convertPlasmaJsonToPlayJson(payload)
    convertedPayload.validate[ValidationResponse] match {
      case success: JsSuccess[ValidationResponse] =>
      case e: JsError =>
        fail(JsError.toFlatJson(e).toString())
    }
  }
}
