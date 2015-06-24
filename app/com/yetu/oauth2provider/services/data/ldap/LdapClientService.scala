package com.yetu.oauth2provider.services.data.ldap

import com.unboundid.ldap.sdk.{ Attribute, Entry, SearchResultEntry }
import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.data.ldap.models.Client
import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.services.data.interface.IClientService

class LdapClientService(dao: LdapDAO) extends IClientService {

  def saveClient(client: OAuth2Client, ignoreEntryAlreadyExists: Boolean): Unit = {

    val entry = new Entry(Client.getDN(client.clientId))
    entry.addAttribute(Client.getObjectClass())
    entry.addAttribute(new Attribute(Client.CLIENT_ID, client.clientId))
    entry.addAttribute(new Attribute(Client.CLIENT_SECRET, client.clientSecret))
    entry.addAttribute(new Attribute(Client.CLIENT_NAME, client.clientName))
    entry.addAttribute(new Attribute(Client.CORE_YETU_CLIENT, client.coreYetuClient.toString))

    for (scope <- client.scopes.getOrElse(List())) {
      entry.addAttribute(new Attribute(Client.SCOPE, scope))
    }

    for (redirect <- client.redirectURIs) {
      entry.addAttribute(new Attribute(Client.REDIRECT_URL, redirect))
    }

    for (grantType <- client.grantTypes.getOrElse(List())) {
      entry.addAttribute(new Attribute(Client.GRANT_TYPE, grantType))
    }

    dao.persist(entry, ignoreEntryAlreadyExists)
  }

  def findClient(clientId: String): Option[OAuth2Client] = {
    val searchResultEntry = dao.getEntry(Client.getDN(clientId))
    searchResultEntry match {
      case r: SearchResultEntry => {
        val id = r.getAttribute(Client.CLIENT_ID).getValue
        val secret = r.getAttribute(Client.CLIENT_SECRET).getValue
        val redirects: List[String] = r.getAttribute(Client.REDIRECT_URL).getValues.toList
        val grants: List[String] = r.getAttribute(Client.GRANT_TYPE).getValues.toList
        val scopes: List[String] = r.getAttribute(Client.SCOPE).getValues.toList
        val clientName = r.getAttribute(Client.CLIENT_NAME).getValue
        val coreYetuClient = r.getAttribute(Client.CORE_YETU_CLIENT).getValueAsBoolean
        Some(new OAuth2Client(id, secret, redirects, Some(grants), Some(scopes), clientName, coreYetuClient))
      }
      case _ => None
    }
  }

  def deleteClient(client: OAuth2Client) = {
    dao.deleteEntry(Client.getDN(client.clientId))
  }

  def deleteClient(clientId: String) = {
    dao.deleteEntry(Client.getDN(clientId))
  }

}
