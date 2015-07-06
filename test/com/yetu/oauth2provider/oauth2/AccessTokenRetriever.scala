package com.yetu.oauth2provider.oauth2

import com.yetu.oauth2provider.base.{ AuthRoutesHelper, DefaultTestVariables }
import com.yetu.oauth2provider.oauth2.models.{ ClientScopes, OAuth2Client }
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.utils.Config
import com.yetu.oauth2provider.utils.Config._
import com.yetu.oauth2resource.utils.RoutesHelper

trait AccessTokenRetriever extends DefaultTestVariables with TestRegistry with RoutesHelper with AuthRoutesHelper {

  /**
   * implement me!
   */
  def getAccessToken(): String

  def implementationId: String = this.getClass.getCanonicalName

  def prepareClientAndUser(scopes: List[String] = List(SCOPE_BASIC),
                           clientId: String = integrationTestClientId,
                           coreYetuClient: Boolean = false,
                           deleteSaveTestUser: Boolean = true,
                           clientRedirectUrls: List[String] = List("http://dummyRedirectUrl"),
                           grantPermissions: Boolean = true) = {

    val client = OAuth2Client(clientId, integrationTestSecret,
      redirectURIs = clientRedirectUrls,
      grantTypes = Config.OAuth2.Defaults.defaultGrantTypes,
      clientName = "Integration Test client",
      coreYetuClient = coreYetuClient)

    //Persist User
    val userPassParameters = Map(
      "username" -> Seq(testUser.email.get),
      "password" -> Seq(testUserPassword)
    )
    if (deleteSaveTestUser) {
      personService.deleteUser(testUser.userId)
      personService.addUser(testUser)
    }

    //Persist client
    clientService.deleteClient(client)
    clientService.saveClient(client)

    //Persist permissions
    val clientPermission = ClientScopes(clientId, Some(scopes))
    permissionService.deletePermission(testUser.userId, clientPermission.clientId)

    if (grantPermissions) {
      permissionService.savePermission(testUser.userId, clientPermission)
    }

    (client, userPassParameters)
  }

}

