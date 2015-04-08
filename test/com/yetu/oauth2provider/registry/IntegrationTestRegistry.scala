package com.yetu.oauth2provider.registry

/**
 * use the real LDAP for these integration tests.
 */
trait IntegrationTestRegistry extends ControllerRegistry with LdapDataServices {

}

object IntegrationTestRegistry extends IntegrationTestRegistry