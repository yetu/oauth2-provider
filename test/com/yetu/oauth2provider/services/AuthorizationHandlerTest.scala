package com.yetu.oauth2provider
package services

import com.yetu.oauth2provider.base.BaseSpec
import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import org.scalatest.concurrent.{ AsyncAssertions, ScalaFutures }
import org.scalatestplus.play.OneAppPerSuite

import scalaoauth2.provider.ClientCredential

class AuthorizationHandlerTest extends BaseSpec with OneAppPerSuite with ScalaFutures with AsyncAssertions {

  //with ParallelTestExecution

  "authorization authorizationHandler" must {

    val clientId = "kfentbajwfkwuwv"
    val clientSecret = "afajbfkjkweaf"
    val grantType = "code"
    val redirectUrls = List("http://www.a.com")
    val scopes = Some(List("basic"))
    val name = "clientName"

    "validate a valid client ID/secret combination" in {
      clientService.deleteClient(clientId)
      clientService.saveClient(new OAuth2Client(clientId, clientSecret, redirectUrls, Some(List(grantType)), clientName = name, coreYetuClient = true))
      whenReady(authorizationHandler.validateClient(ClientCredential(clientId, Some(clientSecret)), grantType)) {
        result => result mustBe true
      }
    }

    "not validate an invalid client secret" in {
      whenReady(authorizationHandler.validateClient(ClientCredential(clientId, Some("invalidSecret")), grantType)) { result => result mustBe false
      }
    }

    "not validate an non-existing client ID" in {
      whenReady(authorizationHandler.validateClient(ClientCredential("invalidClientId", Some(clientSecret)), grantType)) { result => result mustBe false
      }
    }

    " validate an valid grantType" in {
      clientService.deleteClient(clientId)
      clientService.saveClient(new OAuth2Client(clientId, clientSecret, redirectUrls, Some(List(grantType)), clientName = name, coreYetuClient = true))
      whenReady(authorizationHandler.validateClient(ClientCredential(clientId, Some(clientSecret)), grantType)) { result => result mustBe true
      }
    }

    "not validate an invalid grantType" in {
      clientService.deleteClient(clientId)
      clientService.saveClient(new OAuth2Client(clientId, clientSecret, redirectUrls, Some(List(grantType)), clientName = name, coreYetuClient = true))
      whenReady(authorizationHandler.validateClient(ClientCredential(clientId, Some(clientSecret)), "otherGrantType")) { result => result mustBe false
      }
    }

  }

}
