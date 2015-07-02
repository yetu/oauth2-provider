package com.yetu.oauth2provider.services.data.ldap

import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.data.ldap.models.{ ClientPermission => LdapClientPermission }
import com.yetu.oauth2provider.oauth2.models.ClientPermission
import com.yetu.oauth2provider.services.data.interface.IPermissionService

import scala.concurrent.Future

class LdapPermissionService(dao: LdapDAO) extends IPermissionService {

  /*
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

  def savePermission(email: String, clientPermission: ClientPermission) = {
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
  */

  override def savePermission(userId: String, clientPermission: ClientPermission, amend: Boolean): Future[Unit] = ???

  override def deletePermission(userId: String, clientId: String): Future[Unit] = ???

  override def findPermission(userId: String, clientId: String): Future[Option[ClientPermission]] = ???
}
