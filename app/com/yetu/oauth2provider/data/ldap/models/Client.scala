package com.yetu.oauth2provider.data.ldap.models

import com.unboundid.ldap.sdk.Attribute

object Client extends LdapObject {

  def getRDN(): String = {
    "ou=yetuApps"
  }

  def getObjectClass(): Attribute = {
    new Attribute("objectClass", "yetuApp")
  }

  val CLIENT_ID = "clientId"
  val CLIENT_NAME = "clientName"
  val CLIENT_SECRET = "clientSecret"
  val CORE_YETU_CLIENT = "coreYetuClient"
  val REDIRECT_URL = "redirectURIs"
  val GRANT_TYPE = "grantType"
  val SCOPE = "scope"
  val UID = "uid"

}
