package com.yetu.oauth2provider.services.data.ldap

import com.unboundid.ldap.sdk.{ Attribute, Entry, SearchResultEntry }
import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.data.ldap.models.{ Client, ClientPermission => LdapClientPermission }
import com.yetu.oauth2provider.oauth2.models.ClientPermission
import com.yetu.oauth2provider.services.data.interface.IPermissionService

class LdapPermissionService(dao: LdapDAO) extends IPermissionService {

  def findPermission(userId: String, clientId: String): Option[ClientPermission] = {
    val searchResult = dao.getEntry(LdapClientPermission.getClientDN(clientId, userId))
    searchResult match {
      case r: SearchResultEntry => {
        val scopes: List[String] = r.getAttribute(Client.SCOPE).getValues.toList
        Some(new ClientPermission(clientId, Some(scopes)))
      }
      case _ => None
    }
  }

  def savePermission(email: String, clientPermission: ClientPermission): Unit = {
    //ou=permissions does not exist it will give error so first create that if is not
    val permissionTree = new Entry(LdapClientPermission.getDN(email))
    permissionTree.addAttribute(LdapClientPermission.getObjectClass())
    dao.persist(permissionTree, ignoreEntryAlreadyExists = true)

    val entry = new Entry(LdapClientPermission.getClientDN(clientPermission.clientId, email))
    entry.addAttribute(LdapClientPermission.getClientObjectClass())

    for (scope <- clientPermission.scopes.getOrElse(List())) {
      entry.addAttribute(new Attribute("scope", scope))
    }

    dao.persist(entry, ignoreEntryAlreadyExists = true)
  }

  def deletePermission(email: String, clientId: String) = {
    dao.deleteEntry(LdapClientPermission.getClientDN(clientId, email))
  }

}
