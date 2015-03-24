package com.yetu.oauth2provider
package registry

import com.softwaremill.macwire.MacwireMacros._
import com.yetu.oauth2provider.controllers.OAuth2ImplicitControllerHelper
import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.oauth2.MyCustomTokenEndpoint
import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.oauth2.services._
import com.yetu.oauth2provider.services.data._
import com.yetu.oauth2provider.services.data.iface._
import com.yetu.oauth2provider.signature.services.SignatureService
import com.yetu.oauth2provider.utils.{ Config, JsonWebTokenGenerator }
import securesocial.core.RuntimeEnvironment
import securesocial.core.providers.utils.PasswordHasher
import securesocial.core.providers.utils.PasswordHasher.Default
import com.yetu.oauth2provider.signature.services.SignatureService
import securesocial.core.services.UserService
import services.data.MemoryUserService

import scalaoauth2.provider.TokenEndpoint

trait InMemoryDataServices {
  lazy val clientService: IClientService = wire[MemoryClientService]
  lazy val permissionService: IPermissionService = wire[MemoryPermissionService]

  lazy val publicKeyService: IPublicKeyService = new MemoryPublicKeyService

  lazy val personService: IPersonService = wire[MemoryPersonService]
  lazy val myUserService: UserService[YetuUser] = new MemoryUserService()
}

trait LdapDataServices {
  lazy val dao: LdapDAO = wire[LdapDAO]
  lazy val clientService: IClientService = wire[LdapClientService]
  lazy val permissionService: IPermissionService = wire[LdapPermissionService]

  lazy val publicKeyService: IPublicKeyService = new LdapPublicKeyService(new LdapPersonService(dao))

  lazy val personService: IPersonService = wire[LdapPersonService]
  lazy val myUserService: UserService[YetuUser] = new LdapUserService(dao)
}

trait ServicesRegistry {

  def clientService: IClientService
  def permissionService: IPermissionService

  def publicKeyService: IPublicKeyService

  def personService: IPersonService
  def myUserService: UserService[YetuUser]

  lazy val authCodeAccessTokenService: IAuthCodeAccessTokenService = wire[MemoryAuthCodeAccessTokens]

  lazy val scopeService: ScopeService = wire[ScopeService]
  lazy val validationService: ValidationService = wire[ValidationService]
  lazy val jsonWebTokenGenerator: JsonWebTokenGenerator = wire[JsonWebTokenGenerator]

  /**
   * TODO: probably we should use salting with bcrypt for the passwords. The default implementation does not use salts.
   * see securesocial.core.RuntimeEnvironment for current mapping of passwordhashers.
   */
  lazy val defaultPasswordHasher = new PasswordHasher.Default()
  lazy val yetuPasswordHashers: Map[String, PasswordHasher] = Map(defaultPasswordHasher.id -> defaultPasswordHasher)

  // wire other services based on the above data services
  // need to be wired manually due to limitation in macwire to recognize services by name:
  /**
   *
   * [error] /Users/joe/Documents/git/apphome-oauth2provider/app/com/yetu/oauth2provider/registry/ServicesRegistry.scala:37:
   * Found multiple values of type [com.yetu.oauth2provider.services.data.iface.IPersonService]: [List(myUserService, personService)]
   * [error]   lazy val authorizeService = wire[AuthorizeService]
   */

  lazy val authorizationHandler = new AuthorizationHandler(authCodeAccessTokenService, clientService, personService, yetuPasswordHashers, jsonWebTokenGenerator)
  //  lazy val authorizationHandler = wire[AuthorizationHandler]

  lazy val authorizeService = new AuthorizeService(authCodeAccessTokenService, personService, scopeService, permissionService)
  //wire[AuthorizeService]

  lazy val authorizeErrorHandler = new AuthorizeErrorHandler(clientService, personService, scopeService, permissionService)
  //wire[AuthorizeErrorHandler]

  val signatureService = new SignatureService[YetuUser](personService, publicKeyService)

  val signatureHandler = new SignatureHandler[YetuUser](signatureService)

  val implicitGrantFlowService = new ImplicitGrantFlowService[YetuUser](personService)

  val implicitGrantFlowHandler = new ImplicitGrantFlowHandler[YetuUser](implicitGrantFlowService)

  val myCustomTokenEndpoint: TokenEndpoint = new MyCustomTokenEndpoint(signatureHandler, implicitGrantFlowHandler)

  val oAuth2ImplicitController = new OAuth2ImplicitControllerHelper(myCustomTokenEndpoint)

}

