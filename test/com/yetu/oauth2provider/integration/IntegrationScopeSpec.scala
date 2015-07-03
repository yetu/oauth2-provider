package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import com.yetu.oauth2provider.utils.Config._
import play.api.test.Helpers._

/**
 *
 * Integration test from login via /authorize to get the authorization code,
 * then using that to check for various permission scopes
 *
 */
class IntegrationScopeSpec extends IntegrationBaseSpec with AuthorizationCodeFlow {

  s"Integration test which starts with POSTing username and password to $loginUrlWithUserPass endpoint" must {

    s"prevent any valid yetu core clients to access information with different scopes which does not match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientScopes = Some(List(SCOPE_BASIC)), queryScopes = Some(List(SCOPE_CONTACT)), coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"prevent any valid third party clients to access information with different scopes which is not match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientScopes = Some(List(SCOPE_BASIC)), queryScopes = Some(List(SCOPE_CONTACT)), coreYetuClient = false)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid yetu core clients to access information with correct scopes which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientScopes = Some(List(SCOPE_BASIC)), queryScopes = Some(List(SCOPE_BASIC)), coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid yetu core clients to access information with multiple scopes as long as all of them are registered" in {

      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId,
        clientScopes = Some(List(SCOPE_BASIC, SCOPE_CONTACT, SCOPE_HOUSEHOLD_READ)),
        queryScopes = Some(List(SCOPE_BASIC, SCOPE_CONTACT)),
        coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"reject any valid yetu core clients to access information with multiple scopes if one of them is invalid" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId,
        clientScopes = Some(List(SCOPE_BASIC, SCOPE_CONTACT, SCOPE_HOUSEHOLD_READ)),
        queryScopes = Some(List(SCOPE_BASIC, SCOPE_CONTACT, SCOPE_CONTROLCENTER)),
        coreYetuClient = true)

      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid third party clients to access information with correct scopes which is match with client" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientScopes = Some(List(SCOPE_BASIC)), queryScopes = Some(List(SCOPE_BASIC)), coreYetuClient = false)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid yetu core clients to access information with a scope which is one of the scopes the client has registered" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientScopes = Some(List(SCOPE_ID, SCOPE_BASIC, SCOPE_HOUSEHOLD_READ)), queryScopes = Some(List(SCOPE_BASIC)), coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"allow any valid third party clients to access information with a scope  which is one of the scopes the client has registered" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientScopes = Some(List(SCOPE_ID, SCOPE_BASIC, SCOPE_HOUSEHOLD_READ)), queryScopes = Some(List(SCOPE_BASIC)), coreYetuClient = false)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"prevent any valid yetu core clients to access information with a scope which is NOT one of the scopes the client has registered" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate(integrationTestClientId, clientScopes = Some(List(SCOPE_ID, SCOPE_BASIC, SCOPE_HOUSEHOLD_READ)), queryScopes = Some(List(SCOPE_CONTACT)), coreYetuClient = true)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

    s"prevent any valid third party clients to access information with a scope which is NOT one of the scopes the client has registered" in {
      val responseAuthorization = registerClientAndUserAndAuthenticate("otherClientId", clientScopes = Some(List(SCOPE_ID, SCOPE_BASIC, SCOPE_HOUSEHOLD_READ)), queryScopes = Some(List(SCOPE_CONTACT)), coreYetuClient = false)
      status(responseAuthorization) mustEqual SEE_OTHER
    }

  }

}
