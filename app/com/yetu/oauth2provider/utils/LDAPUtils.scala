package com.yetu.oauth2provider
package utils

import com.unboundid.ldap.sdk.{ Modification, SearchResultEntry }

object LDAPUtils {

  /**
   * This method checks whether SearchResultEntry has an given attribute or not.
   * @param entry
   * @param attributeName
   * @return
   */
  def getAttribute(entry: SearchResultEntry, attributeName: String): Option[String] = {
    if (entry.getAttribute(attributeName) != null)
      Some(entry.getAttribute(attributeName).getValue)
    else
      None
  }

  /**
   * Method adds an option of  Modification and adds it into list of modification. It is used for making more than one modification to LDAP entry
   * @param mods
   * @param modification
   * @return
   */
  def addToModList(mods: List[Modification], modification: Option[Modification]): List[Modification] = {
    val result = modification match {
      case Some(x) => x :: mods
      case None    => mods
    }
    result
  }
}
