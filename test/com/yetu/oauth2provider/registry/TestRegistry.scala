package com.yetu.oauth2provider.registry

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data._
import com.yetu.oauth2provider.services.data.iface.{ IClientService, IPermissionService, IPersonService, IPublicKeyService }
import securesocial.core.services.UserService

trait TestRegistry extends ControllerRegistry {

  import com.softwaremill.macwire.MacwireMacros._

  override lazy val clientService: IClientService = wire[MemoryClientService]
  override lazy val permissionService: IPermissionService = wire[MemoryPermissionService]
  override lazy val personService: IPersonService = wire[MemoryPersonService]

  override lazy val myUserService: UserService[YetuUser] = new MemoryUserService

  override lazy val publicKeyService: IPublicKeyService = wire[MemoryPublicKeyService]

}

object TestRegistry extends TestRegistry

