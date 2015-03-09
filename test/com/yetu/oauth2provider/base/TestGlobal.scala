package com.yetu.oauth2provider.base

import com.softwaremill.macwire.Macwire
import com.yetu.oauth2provider.registry.TestRegistry
import play.api.GlobalSettings

object TestGlobal extends GlobalSettings with Macwire {

  val diRegistry = wiredInModule(TestRegistry)

  override def getControllerInstance[A](controllerClass: Class[A]): A = {

    diRegistry.lookupSingleOrThrow(controllerClass)

  }

}