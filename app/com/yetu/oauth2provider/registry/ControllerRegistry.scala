package com.yetu.oauth2provider.registry

import com.softwaremill.macwire.MacwireMacros._
import com.yetu.oauth2provider.controllers._
import com.yetu.oauth2provider.controllers.authentication._
import com.yetu.oauth2provider.controllers.setup.SetupController
import com.yetu.oauth2provider.events.LogoutEventListener
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.RiakCacheService
import securesocial.core.{ EventListener, RuntimeEnvironment }
import securesocial.core.authenticator.{ AuthenticatorStore, HttpHeaderAuthenticatorBuilder }
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.providers.utils.PasswordValidator
import securesocial.core.services.{ CacheService, AuthenticatorService, UserService }

import scala.collection.immutable.ListMap

/**
 * This object holds the complete list of controller that should be used in application.
 * Controllers will be instantiated via dependency injection library macwire
 */
trait ControllerRegistry extends ServicesRegistry {
  lazy val oAuth2Auth = wire[OAuth2Auth]

  lazy val oAuth2ResourceServer = wire[OAuth2ResourceServer]
  lazy val oAuth2TrustedServer = wire[OAuth2TrustedServer]
  lazy val oAuth2Validation = wire[OAuth2Validation]

  lazy val yetuMailTemplates = wire[YetuMailTemplates]
  lazy val yetuViewTemplates = wire[YetuViewTemplates]
  lazy val loginPage = wire[LoginPage]
  lazy val loginApi = wire[LoginApi]
  lazy val passwordChange = wire[PasswordChange]
  lazy val passwordReset = wire[PasswordReset]
  lazy val providerController = wire[ProviderController]
  lazy val registration = wire[Registration]
  lazy val signatureAuthenticationProvider = new SignatureAuthenticationProvider[YetuUser](signatureService)

  lazy val application = play.api.Play.current
  lazy val logoutEventListener = wire[LogoutEventListener]

  lazy val env: RuntimeEnvironment[YetuUser] = MyRuntimeEnvironment

  lazy val setupController = wire[SetupController]

  object MyRuntimeEnvironment extends RuntimeEnvironment.Default[YetuUser] {
    override lazy val cacheService: CacheService = cacheConnection
    override lazy val mailTemplates = yetuMailTemplates
    override lazy val viewTemplates = yetuViewTemplates
    override lazy val userService: UserService[YetuUser] = myUserService
    override lazy val passwordHashers = yetuPasswordHashers
    override lazy val passwordValidator: PasswordValidator = new YetuPasswordValidator()
    override lazy val eventListeners: List[EventListener[YetuUser]] = List(logoutEventListener)
    override lazy val providers = ListMap(
      include(new UsernamePasswordProvider[YetuUser](userService, avatarService, viewTemplates, passwordHashers)),
      include(signatureAuthenticationProvider)
    )

    override lazy val authenticatorService = new AuthenticatorService(
      new CustomCookieAuthenticatorBuilder[YetuUser](new AuthenticatorStore.Default(cacheService), idGenerator),
      new HttpHeaderAuthenticatorBuilder[YetuUser](new AuthenticatorStore.Default(cacheService), idGenerator)
    )
  }
}

object PersistentControllerRegistry extends ControllerRegistry with PersistentDataServices
object MemoryControllerRegistry extends ControllerRegistry with InMemoryDataServices
