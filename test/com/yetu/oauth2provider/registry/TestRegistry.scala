package com.yetu.oauth2provider.registry

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data._
import com.yetu.oauth2provider.services.data.iface.{ IClientService, IPermissionService, IPersonService, IPublicKeyService }
import securesocial.core.services.UserService

trait TestRegistry extends ControllerRegistry with InMemoryDataServices {

}

object TestRegistry extends TestRegistry

