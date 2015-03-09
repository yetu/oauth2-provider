package com.yetu.oauth2provider.data.ldap.models

import com.unboundid.ldap.sdk.Attribute

object Root extends LdapObject {

  def getRDN(): String = {
    "dc=yetu,dc=com"
  }

  def getObjectClass(): Attribute = ???
}
