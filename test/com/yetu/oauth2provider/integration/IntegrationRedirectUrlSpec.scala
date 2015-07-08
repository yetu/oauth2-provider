package com.yetu.oauth2provider.integration

import java.net.URLEncoder

import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import play.api.test.Helpers._

/**
 *
 * Integration test from login via /authorize to get the authorization code,
 * then using that to check for various redirect urls
 *
 */
class IntegrationRedirectUrlSpec extends IntegrationBaseSpec with AuthorizationCodeFlow {

  s"Integration test which starts with POSTing username and password to $loginUrlWithUserPass endpoint" must {

    s"prevent any valid yetu core clients to access information with different redirectUrl which is not match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(s"$defaultRedirectUrl Invalid"), coreYetuClient = true)
      status(responseAuthorization) mustEqual UNAUTHORIZED
    }

    s"prevent any valid third party clients to access information with different redirectUrl which is not match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(s"$defaultRedirectUrl Invalid"), coreYetuClient = false)
      status(responseAuthorization) mustEqual UNAUTHORIZED
    }

    s"allow any valid yetu core clients to access information with correct redirectUrl which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(defaultRedirectUrl), coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid third party clients to access information with correct redirectUrl which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(defaultRedirectUrl), coreYetuClient = false)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid yetu core clients to access information with correct redirectUrl(encoded) which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(URLEncoder.encode(defaultRedirectUrl, "UTF-8")), coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid third party clients to access information with correct redirectUrl(encoded) which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientRedirectUrls = List(defaultRedirectUrl), queryRedirectUrl = Some(URLEncoder.encode(defaultRedirectUrl, "UTF-8")), coreYetuClient = false)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid yetu core clients(with multiple redirectUrl) to access information with correct redirectUrl which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientRedirectUrls = List(defaultRedirectUrl, defaultRedirectUrl2), queryRedirectUrl = Some(defaultRedirectUrl), coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid third party clients(with multiple redirectUrl) to access information with correct redirectUrl which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientRedirectUrls = List(defaultRedirectUrl, defaultRedirectUrl2), queryRedirectUrl = Some(defaultRedirectUrl), coreYetuClient = false)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid yetu core clients(with multiple redirectUrl) to access information with different redirectUrl which is not match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientRedirectUrls = List(defaultRedirectUrl, defaultRedirectUrl2), queryRedirectUrl = Some(s"$defaultRedirectUrl Invalid"), coreYetuClient = true)
      status(responseAuthorization) mustEqual UNAUTHORIZED
    }

    s"allow any valid third party clients(with multiple redirectUrl) to access information with different redirectUrl which is not match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientRedirectUrls = List(defaultRedirectUrl, defaultRedirectUrl2), queryRedirectUrl = Some(s"$defaultRedirectUrl Invalid"), coreYetuClient = false)
      status(responseAuthorization) mustEqual UNAUTHORIZED
    }

  }

}
