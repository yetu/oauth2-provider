package com.yetu.oauth2provider
package utils

import com.typesafe.config.ConfigFactory
import com.yetu.oauth2provider.data.riak.RiakConnection
import org.joda.time.DateTimeConstants.MILLIS_PER_SECOND
import play.api.Play
import play.api.Play.current
import play.api.mvc.DiscardingCookie

object Config {

  lazy val minimumStateLength = Play.configuration.getInt("authorize.state.minLength").get
  lazy val maximumStateLength = Play.configuration.getInt("authorize.state.maxLength").get

  /**
   * Returns the default redirect url for current stage
   */
  lazy val redirectAfterLogin = Play.configuration.getString("redirect.afterlogin").get

  case class GoogleAnalytics(enabled: Boolean, trackingId: String)

  lazy val googleAnalytics = GoogleAnalytics(Play.configuration.getBoolean("webanalytics.google.enabled").get, Play.configuration.getString("webanalytics.google.trackingId").get)

  lazy val redirectURICheckingEnabled = Play.configuration.getBoolean("security.redirectURICheckingEnabled").get

  lazy val publicUrl = Play.configuration.getString("yetu.publicUrl").get

  lazy val ldapHost = Play.configuration.getString("ldap.hostname").get
  lazy val ldapPort = Play.configuration.getInt("ldap.port").get
  lazy val ldapUser = Play.configuration.getString("ldap.username").get
  lazy val ldapPassword = Play.configuration.getString("ldap.password").get
  //TODO: don't use the root account for ldap!
  lazy val ldapNumberOfConnections = Play.configuration.getInt("ldap.numberOfConnections").get

  // OAUTH2 constants
  val GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code"
  val GRANT_TYPE_RESOURCE_OWNER_PASSWORD = "password"
  val GRANT_TYPE_SIGNATURE = "signature"
  val GRANT_TYPE_TOKEN = "token"
  val GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials"

  //OAuth scopes
  val SCOPE_ID = "id"
  val SCOPE_BASIC = "basic"
  val SCOPE_EVENTS = "events"
  val SCOPE_CONTACT = "contact"
  val SCOPE_PASSWORD = "password"
  val SCOPE_REGISTRATION_INFO = "registrationInfo"
  val SCOPE_HOUSEHOLD_READ = "householdRead"
  val SCOPE_HOUSEHOLD_WRITE = "householdWrite"
  val SCOPE_HOUSEHOLD_GENERATE = "householdGenerate"

  /**
   * combination of REGISTRATION_INFO and HOUSEHOLD_READ
   * This scope is redundant once we can allow more than one scope on the query string
   * (requires change in the nulab third-party library)
   */
  val SCOPE_CONTROLCENTER = "controlcenter"

  object OAuth2 {

    object Defaults {
      val defaultGrantTypes = Some(List(GRANT_TYPE_AUTHORIZATION_CODE, GRANT_TYPE_RESOURCE_OWNER_PASSWORD, GRANT_TYPE_SIGNATURE, GRANT_TYPE_TOKEN))

    }

    lazy val jsonWebTokenPrivateKeyFilename = Play.configuration.getString("security.jsonWebToken.privateKeyFilename").get
    lazy val jsonWebTokenPublicKeyFilename = Play.configuration.getString("security.jsonWebToken.publicKeyFilename").get

    lazy val accessTokenExpirationInSeconds: Int = Play.configuration.getInt("security.expireTimes.accessTokenInSeconds").get

    lazy val signatureDateExpirationInMilliseconds = Play.configuration.getLong("security.expireTimes.signatureInSeconds").get * MILLIS_PER_SECOND

    lazy val authTokenLength: Int = Play.configuration.getInt("security.authCode.length").get

  }

  object SessionStatusCookie {

    import play.api.Play.current
    import securesocial.core.authenticator.CookieAuthenticator

    lazy val cookieName = Play.application.configuration.getString("session.statusCookie.name").getOrElse("status-cookie")
    lazy val cookiePath = Play.application.configuration.getString("session.statusCookie.path").getOrElse(CookieAuthenticator.cookiePath)
    lazy val cookieDomain = CookieAuthenticator.cookieDomain

    lazy val cookieHttpOnly: Boolean = Play.application.configuration.getBoolean("session.statusCookie.httpOnly").getOrElse(false)
    lazy val idleTimeoutInMinutes = CookieAuthenticator.idleTimeout
    lazy val absoluteTimeoutInSeconds = CookieAuthenticator.absoluteTimeoutInSeconds

    lazy val cookieSecure = CookieAuthenticator.cookieSecure
    lazy val discardingCookie: DiscardingCookie = DiscardingCookie(cookieName, cookiePath, cookieDomain, cookieSecure)
  }

  object FrontendConfiguration {
    lazy val setupDownloadUrlMac = Play.configuration.getString("frontendConfig.setupDownloadUrlMac").get
    lazy val setupDownloadUrlWin = Play.configuration.getString("frontendConfig.setupDownloadUrlWin").get
  }

  object YetuMessageEvents {
    lazy val logoutEventName = Play.configuration.getString("yetu.events.logoutEvent").get
    lazy val clientId = Play.configuration.getString("yetu.events.clientId").get
  }

  // play.configuration requires a started play app; however this configuration value needs to eb read before
  // application start. Use the standard ConfigFactory (loading reference.conf / application.conf)
  val config = ConfigFactory.load()
  val persist = config.getBoolean("persist")

  val minimumPasswordLength = config.getInt("securesocial.userpass.minimumPasswordLength")

  val InvalidPasswordMessage = "securesocial.signup.invalidPassword"

  object ProductionRiakSettings extends RiakConnection {
    override def host: String = config.getString("riak.production.host")
    override def port: Int = config.getInt("riak.production.port")
    override def bucketName: String = config.getString("riak.production.authbucket")
  }

  object TestRiakSettings extends RiakConnection {
    override def host: String = config.getString("riak.test.host")
    override def port: Int = config.getInt("riak.test.port")
    override def bucketName: String = config.getString("riak.test.authbucket")
  }

}

