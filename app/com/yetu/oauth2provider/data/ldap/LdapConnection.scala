package com.yetu.oauth2provider.data.ldap

import com.unboundid.ldap.sdk.{ LDAPConnection, LDAPConnectionPool }
import com.yetu.oauth2provider.utils.Config._
import play.api.Logger

/**
 * ConnectionHelper class to manage connection pool to retrieve and release a connection to LDAP
 */
object LdapConnection {

  private val connectionPool = createConnectionPool

  private def createConnectionPool = {
    val connection = new LDAPConnection()
    connection.connect(ldapHost, ldapPort)
    connection.bind(ldapUser, ldapPassword)
    val connectionPool = new LDAPConnectionPool(connection, ldapNumberOfConnections)
    Logger.debug("connection pool created " + connectionPool.getConnectionPoolStatistics)
    connectionPool
  }

  def getConnection() = {
    connectionPool.getConnection
  }

  def release(connection: LDAPConnection) = {
    connectionPool.releaseConnection(connection)
  }

  def shutDown() = {
    connectionPool.close()
  }
}
