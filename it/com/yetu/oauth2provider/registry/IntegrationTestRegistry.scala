package com.yetu.oauth2provider.registry

import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data._
import com.yetu.oauth2provider.services.data.iface.{IPublicKeyService, IPersonService, IPermissionService, IClientService}
import securesocial.core.services.UserService


/**
 * use the real LDAP for these integration tests.
 */
trait IntegrationTestRegistry extends ControllerRegistry {

  import com.softwaremill.macwire.MacwireMacros._

  override lazy val dao: LdapDAO = wire[LdapDAO]
  override lazy val clientService: IClientService = wire[LdapClientService]
  override lazy val permissionService: IPermissionService = wire[LdapPermissionService]

  override lazy val publicKeyService: IPublicKeyService = new LdapPublicKeyService(new LdapPersonService(dao))

  override lazy val personService: IPersonService = wire[LdapPersonService]
  override lazy val myUserService: UserService[YetuUser] = new LdapUserService(dao)

}

object IntegrationTestRegistry extends IntegrationTestRegistry