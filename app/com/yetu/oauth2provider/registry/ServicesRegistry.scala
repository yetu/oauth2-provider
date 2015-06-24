package com.yetu.oauth2provider
package registry

import com.softwaremill.macwire.MacwireMacros._
import com.yetu.oauth2provider.controllers.OAuth2ImplicitControllerHelper
import com.yetu.oauth2provider.controllers.authentication.CustomCookieAuthenticator
import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.oauth2.OAuth2TokenEndpoint
import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.oauth2.services._
import com.yetu.oauth2provider.services.data.interface._
import com.yetu.oauth2provider.services.data.ldap._
import com.yetu.oauth2provider.services.data.memory._
import com.yetu.oauth2provider.services.data.riak.{ RiakAuthCodeAccessTokens, RiakAuthenticatorStore, RiakMailTokenService }
import com.yetu.oauth2provider.signature.services.SignatureService
import com.yetu.oauth2provider.utils.Config.RiakSettings
import com.yetu.oauth2provider.utils.JsonWebTokenGenerator
import securesocial.core.authenticator.{ AuthenticatorStore, HttpHeaderAuthenticator }
import securesocial.core.providers.utils.PasswordHasher
import securesocial.core.services.{ CacheService, UserService }

import scala.concurrent.ExecutionContext.Implicits.global
import scalaoauth2.provider.TokenEndpoint

trait InMemoryDataServices {
  lazy val clientService: IClientService = wire[MemoryClientService]
  lazy val permissionService: IPermissionService = wire[MemoryPermissionService]

  lazy val publicKeyService: IPublicKeyService = new MemoryPublicKeyService

  lazy val personService: IPersonService = wire[MemoryPersonService]
  lazy val myUserService: UserService[YetuUser] = wire[MemoryUserService]

  lazy val databaseImplementationName: String = "In-Memory database"

  lazy val authCodeAccessTokenService: IAuthCodeAccessTokenService = wire[MemoryAuthCodeAccessTokens]
  lazy val mailTokenService: IMailTokenService = wire[MemoryMailTokenService]

  lazy val httpAuthStore: AuthenticatorStore[HttpHeaderAuthenticator[YetuUser]] =
    new AuthenticatorStore.Default(new CacheService.Default)

  lazy val cookieAuthStore: AuthenticatorStore[CustomCookieAuthenticator[YetuUser]] =
    new AuthenticatorStore.Default(new CacheService.Default)
}

trait PersistentDataServices {

  lazy val dao: LdapDAO = wire[LdapDAO]
  lazy val clientService: IClientService = wire[LdapClientService]
  lazy val permissionService: IPermissionService = wire[LdapPermissionService]

  lazy val publicKeyService: IPublicKeyService = new LdapPublicKeyService(new LdapPersonService(dao))

  lazy val personService: IPersonService = wire[LdapPersonService]
  lazy val myUserService: UserService[YetuUser] = wire[LdapUserService]

  lazy val databaseImplementationName: String = "LDAP database"

  lazy val riakConnection: RiakConnection = RiakSettings

  lazy val authCodeAccessTokenService: IAuthCodeAccessTokenService = wire[RiakAuthCodeAccessTokens]
  lazy val mailTokenService: IMailTokenService = wire[RiakMailTokenService]

  lazy val httpAuthStore: AuthenticatorStore[HttpHeaderAuthenticator[YetuUser]] =
    wire[RiakAuthenticatorStore[HttpHeaderAuthenticator[YetuUser]]]

  lazy val cookieAuthStore: AuthenticatorStore[CustomCookieAuthenticator[YetuUser]] =
    wire[RiakAuthenticatorStore[CustomCookieAuthenticator[YetuUser]]]
}

trait ServicesRegistry {

  def authCodeAccessTokenService: IAuthCodeAccessTokenService
  def mailTokenService: IMailTokenService

  def clientService: IClientService
  def permissionService: IPermissionService

  def publicKeyService: IPublicKeyService

  def personService: IPersonService
  def myUserService: UserService[YetuUser]

  def httpAuthStore: AuthenticatorStore[HttpHeaderAuthenticator[YetuUser]]
  def cookieAuthStore: AuthenticatorStore[CustomCookieAuthenticator[YetuUser]]

  lazy val scopeService: ScopeService = wire[ScopeService]
  lazy val validationService: ValidationService = wire[ValidationService]
  lazy val jsonWebTokenGenerator: JsonWebTokenGenerator = wire[JsonWebTokenGenerator]

  /**
   * TODO: probably we should use salting with bcrypt for the passwords. The default implementation does not use salts.
   * see securesocial.core.RuntimeEnvironment for current mapping of passwordhashers.
   */
  lazy val defaultPasswordHasher = new PasswordHasher.Default()
  lazy val yetuPasswordHashers: Map[String, PasswordHasher] = Map(defaultPasswordHasher.id -> defaultPasswordHasher)

  lazy val authorizationHandler: AuthorizationHandler = wire[AuthorizationHandler]

  lazy val authorizeService: AuthorizeService = wire[AuthorizeService]

  lazy val authorizeErrorHandler = wire[AuthorizeErrorHandler]

  val signatureService = new SignatureService[YetuUser](personService, publicKeyService)

  val signatureHandler = new SignatureHandler[YetuUser](signatureService)

  val implicitGrantFlowService = new ImplicitGrantFlowService[YetuUser](personService)

  val implicitGrantFlowHandler = new ImplicitGrantFlowHandler[YetuUser](implicitGrantFlowService)

  val oAuth2TokenEndpoint: TokenEndpoint = new OAuth2TokenEndpoint(signatureHandler, implicitGrantFlowHandler)

  val oAuth2ImplicitController = new OAuth2ImplicitControllerHelper(oAuth2TokenEndpoint)

}

