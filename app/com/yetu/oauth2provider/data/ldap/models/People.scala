package com.yetu.oauth2provider.data.ldap.models

import com.unboundid.ldap.sdk.{ Attribute, Entry }

object People extends LdapObject {

  def getRDN(): String = {
    "ou=people"
  }

  def getObjectClass(): Attribute = {
    new Attribute("objectClass", "person", "inetOrgPerson", "organizationalPerson", "extensibleObject")
  }

  val FIRST_NAME = "givenName"
  val MEMBER_UID = "memberUid"
  val LAST_NAME = "sn"
  val FULL_NAME = "cn"
  val EMAIL = "mail"
  val USER_PASSWORD = "userPassword"
  val CITY = "city"
  val MOBILE = "mobile"
  val HOME_PHONE = "homePhone"
  val FAX = "facsimileTelephoneNumber"
  val CHAT = "description"
  val HOUSE_NUMBER = "houseNumber"
  val COUNTRY = "co"
  val POSTAL_CODE = "postalCode"
  val STREET = "street"
  val PHOTO = "info"

  val PUBLIC_KEY = "sshPublicKey"
}
