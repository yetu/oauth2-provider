package com.yetu.oauth2provider.data.ldap

import com.unboundid.ldap.sdk._
import play.api.Logger

/**
 *
 *
 * Generic Data Access Object
 */
class LdapDAO {
  def persist(ldapEntry: Entry, ignoreEntryAlreadyExists: Boolean = false): Unit = {
    val c = LdapConnection.getConnection
    try {
      c.add(ldapEntry)
    } catch {
      case e: LDAPException => {
        if (ignoreEntryAlreadyExists && e.getMessage.contains("an entry with that name already exists")) {
          Logger.debug(e.getMessage)
        } else {
          throw e
        }
      }
    } finally {
      LdapConnection.release(c)
    }
  }

  def deleteEntry(dn: String) = {
    val c = LdapConnection.getConnection()
    try {
      c.delete(dn)
    } catch {
      case e: LDAPException => Logger.debug(s"tried to delete non existing entry: ${e.getMessage}")
    } finally {
      LdapConnection.release(c)
    }
  }

  def getEntry(dn: String, attributes: String*): SearchResultEntry = {
    val c = LdapConnection.getConnection()
    try {
      c.getEntry(dn, attributes: _*)
    } catch {
      case e: LDAPException => throw e
    } finally {
      LdapConnection.release(c)
    }
  }

  def modify(dn: String, modification: Modification): LDAPResult = {
    val c = LdapConnection.getConnection()
    try {
      c.modify(dn, modification)
    } catch {
      case e: LDAPException => throw e
    } finally {
      LdapConnection.release(c)
    }
  }

  def modify(dn: String, modifications: Modification*): LDAPResult = {

    val c = LdapConnection.getConnection()
    try {
      c.modify(dn, modifications: _*)
    } catch {
      case e: LDAPException => throw e
    } finally {
      LdapConnection.release(c)
    }
  }

}
