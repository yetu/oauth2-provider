package com.yetu.oauth2provider
package data
package connection

import com.yetu.oauth2provider.utils.Config._
import com.unboundid.ldap.sdk.LDAPConnection
import com.unboundid.ldap.sdk.LDAPConnectionPool
import play.api.Logger

/**
 * ConnectionHelper class to manage connection pool to retrieve and release a connection to LDAP
 */
object ConnectionHelper {

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
