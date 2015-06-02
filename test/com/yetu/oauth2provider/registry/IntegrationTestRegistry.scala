package com.yetu.oauth2provider.registry

import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.utils.Config.TestRiakSettings

/**
 * use the real LDAP and Riak for these integration tests.
 */
trait IntegrationTestRegistry extends ControllerRegistry with PersistentDataServices {
  override lazy val riakConnection: RiakConnection = TestRiakSettings
}

object IntegrationTestRegistry extends IntegrationTestRegistry