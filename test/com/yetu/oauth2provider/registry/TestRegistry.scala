package com.yetu.oauth2provider.registry

trait TestRegistry extends ControllerRegistry with InMemoryDataServices {

}

object TestRegistry extends TestRegistry

