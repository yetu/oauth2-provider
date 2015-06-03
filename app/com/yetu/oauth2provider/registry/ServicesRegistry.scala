package com.yetu.oauth2provider
package registry

import com.softwaremill.macwire.MacwireMacros._
import com.yetu.oauth2provider.controllers.OAuth2ImplicitControllerHelper
import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.oauth2.MyCustomTokenEndpoint
import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.oauth2.services._
import com.yetu.oauth2provider.services.data.{ MemoryUserService, _ }
import com.yetu.oauth2provider.services.data.iface._
import com.yetu.oauth2provider.signature.services.SignatureService
import com.yetu.oauth2provider.utils.Config.ProductionRiakSettings
import com.yetu.oauth2provider.utils.JsonWebTokenGenerator
import securesocial.core.providers.utils.PasswordHasher
import securesocial.core.services.UserService

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
}

trait PersistentDataServices {

  lazy val dao: LdapDAO = wire[LdapDAO]
  lazy val clientService: IClientService = wire[LdapClientService]
  lazy val permissionService: IPermissionService = wire[LdapPermissionService]

  lazy val publicKeyService: IPublicKeyService = new LdapPublicKeyService(new LdapPersonService(dao))

  lazy val personService: IPersonService = wire[LdapPersonService]
  lazy val myUserService: UserService[YetuUser] = wire[LdapUserService]

  lazy val databaseImplementationName: String = "LDAP database"

  lazy val riakConnection: RiakConnection = ProductionRiakSettings

  lazy val authCodeAccessTokenService: IAuthCodeAccessTokenService = wire[RiakAuthCodeAccessTokens]
  lazy val mailTokenService: IMailTokenService = wire[RiakMailTokenService]
}

trait ServicesRegistry {

  def authCodeAccessTokenService: IAuthCodeAccessTokenService
  def mailTokenService: IMailTokenService

  def clientService: IClientService
  def permissionService: IPermissionService

  def publicKeyService: IPublicKeyService

  def personService: IPersonService
  def myUserService: UserService[YetuUser]

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

  val myCustomTokenEndpoint: TokenEndpoint = new MyCustomTokenEndpoint(signatureHandler, implicitGrantFlowHandler)

  val oAuth2ImplicitController = new OAuth2ImplicitControllerHelper(myCustomTokenEndpoint)

}

