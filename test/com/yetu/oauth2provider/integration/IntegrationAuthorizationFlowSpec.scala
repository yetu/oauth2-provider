package com.yetu.oauth2provider.integration

import java.nio.file.{ Paths, Files }

import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import play.api.test.Helpers._
import com.plasmaconduit.jwt.JSONWebToken

class IntegrationAuthorizationFlowSpec extends IntegrationBaseSpec with AuthorizationCodeFlow {

  "IntegrationAuthorizationFlow" must {

    "yield a response authorization Result" in {
      preProcess(integrationTestClientId, clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(s"$defaultRedirectUrl Invalid"), coreYetuClient = true)

    }
  }

  "OAuth2 flows " must {

    oauth2flowImplementations.foreach { implementation =>

      s"support yielding an access_token for the ${implementation.implementationId} flow" in {

        val accessToken = implementation.getAccessToken
        accessToken.size must be > 10
      }

      s"support yielding an access_token which is a JWT and contains expected fields userUUID and clientId for the ${implementation.implementationId} flow" in {

        val accessToken = implementation.getAccessToken

        val publicKey: Array[Byte] = Files.readAllBytes(Paths.get(Config.OAuth2.jsonWebTokenPublicKeyFilename))
        val value = JSONWebToken.verify(publicKey, accessToken).get // throws exception if verification fails

        log(s"contents of JWT token:: $value")

        value.toString must include ("userUUID")
        value.toString must include ("clientId")

      }

    }
  }

}
