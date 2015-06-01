package com.yetu.oauth2provider.registry

import com.softwaremill.macwire.MacwireMacros._
import com.yetu.oauth2provider.controllers._
import com.yetu.oauth2provider.controllers.authentication._
import com.yetu.oauth2provider.controllers.setup.SetupController
import com.yetu.oauth2provider.events.LogoutEventListener
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.LdapUserService
import securesocial.core.{ EventListener, RuntimeEnvironment }
import securesocial.core.authenticator.{ AuthenticatorStore, HttpHeaderAuthenticatorBuilder }
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.providers.utils.PasswordValidator
import securesocial.core.services.{ AuthenticatorService, UserService }

import scala.collection.immutable.ListMap

trait AuthorizationControllerRegistry extends ServicesRegistry {

  lazy val oAuth2ResourceServer = wire[OAuth2ResourceServer]
  lazy val oAuth2TrustedServer = wire[OAuth2TrustedServer]
  lazy val oAuth2Validation = wire[OAuth2Validation]
}

trait AuthenticationControllerRegistry extends ServicesRegistry {

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

  lazy implicit val env: RuntimeEnvironment[YetuUser] = MyRuntimeEnvironment

  lazy val setupController = wire[SetupController]

  object MyRuntimeEnvironment extends RuntimeEnvironment.Default[YetuUser] {
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

/**
 * This object holds the complete list of controller that should be used in application.
 * Controllers will be instantiated via dependency injection library macwire
 */
trait ControllerRegistry extends AuthenticationControllerRegistry with AuthorizationControllerRegistry {

  lazy val oAuth2Auth = wire[OAuth2Auth]
}

object PersistentControllerRegistry extends ControllerRegistry with PersistentDataServices
object MemoryControllerRegistry extends ControllerRegistry with InMemoryDataServices
