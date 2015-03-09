package com.yetu.oauth2provider.services.data

import com.unboundid.ldap.sdk.{ Attribute, Entry, Modification, ModificationType, SearchResultEntry }
import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.data.ldap.models.{ Client, People, ClientPermission => LdapClientPermission }
import com.yetu.oauth2provider.oauth2.models.{ ClientPermission, IdentityId, YetuUser }
import com.yetu.oauth2provider.services.data.iface.{ IPermissionService, IPersonService }
import com.yetu.oauth2provider.utils.{ DateUtility, LDAPUtils, StringUtils, UUIDGenerator }
import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results._
import securesocial.core.{ PasswordInfo, _ }

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

  def savePermission(email: String, clientPermission: ClientPermission, ignoreEntryAlreadyExists: Boolean = false): Unit = {
    //ou=permissions does not exist it will give error so first create that if is not
    val permissionTree = new Entry(LdapClientPermission.getDN(email))
    permissionTree.addAttribute(LdapClientPermission.getObjectClass())
    dao.persist(permissionTree, true)

    val entry = new Entry(LdapClientPermission.getClientDN(clientPermission.clientId, email))
    entry.addAttribute(LdapClientPermission.getClientObjectClass())

    for (scope <- clientPermission.scopes.getOrElse(List())) {
      entry.addAttribute(new Attribute("scope", scope))
    }

    dao.persist(entry, ignoreEntryAlreadyExists)
  }

  def deletePermission(email: String, clientId: String) = {
    dao.deleteEntry(LdapClientPermission.getClientDN(clientId, email))
  }

}
