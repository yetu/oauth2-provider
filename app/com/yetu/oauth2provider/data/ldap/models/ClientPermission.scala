package com.yetu.oauth2provider.data.ldap.models

import com.unboundid.ldap.sdk.Attribute

object ClientPermission extends LdapObject {

  def getRDN(): String = {
    "ou=permissions"
  }

  override def getDN(email: String): String = {
    s"${getRDN()}, ${People.getDN(email)}"
  }

  def getObjectClass(): Attribute = {
    new Attribute("objectClass", "organizationalUnit")
  }

  def getClientDN(clientId: String, email: String): String = {
    s"clientId=${clientId}, ${getDN(email)}"
  }

  def getClientObjectClass(): Attribute = {
    new Attribute("objectClass", "yetuAppPermission")
  }

}
