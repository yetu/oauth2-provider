package com.yetu.oauth2provider.data.ldap.models

import com.unboundid.ldap.sdk.Attribute

trait LdapObject {
  def getRDN(): String

  def getObjectClass(): Attribute

  val objectClassStr = "objectClass"

  def getDN(identifier: String): String = {
    s"uid=${identifier}, ${getRDN()}, ${Root.getRDN()}"
  }

}
