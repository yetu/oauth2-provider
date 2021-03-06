package com.yetu.oauth2provider.registry

import com.softwaremill.macwire.MacwireMacros._
import com.yetu.oauth2provider.controllers._
import com.yetu.oauth2provider.controllers.authentication._
import com.yetu.oauth2provider.controllers.authentication.providers.{ EmailPasswordProvider, SignatureAuthenticationProvider }
import com.yetu.oauth2provider.controllers.setup.SetupController
import com.yetu.oauth2provider.events.LogoutEventListener
import com.yetu.oauth2provider.oauth2.models.YetuUser
import securesocial.core.RuntimeEnvironment
import securesocial.core.authenticator.HttpHeaderAuthenticatorBuilder
import securesocial.core.services.{ AuthenticatorService, UserService }

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

  lazy val yetuMailTemplates: YetuMailTemplates = wire[YetuMailTemplates]
  lazy val yetuViewTemplates: YetuViewTemplates = wire[YetuViewTemplates]
  lazy val loginPage = wire[LoginPage]
  lazy val loginApi = wire[LoginApi]
  lazy val passwordChange = wire[PasswordChange]
  lazy val passwordReset = wire[PasswordReset]
  lazy val providerController = wire[ProviderController]
  lazy val registration = wire[Registration]

  lazy val signatureAuthenticationProvider = new SignatureAuthenticationProvider[YetuUser](signatureService)

  lazy val application = play.api.Play.current
  lazy val logoutEventListener: LogoutEventListener = wire[LogoutEventListener]

  lazy val env: RuntimeEnvironment[YetuUser] = YetuRuntimeEnvironment
  object YetuRuntimeEnvironment extends RuntimeEnvironment.Default[YetuUser] {

    override lazy val mailTemplates = yetuMailTemplates
    override lazy val viewTemplates = yetuViewTemplates
    override lazy val userService: UserService[YetuUser] = yetuUserService
    override lazy val passwordHashers = yetuPasswordHashers
    override lazy val passwordValidator = new YetuPasswordValidator()
    override lazy val eventListeners = List(logoutEventListener)
    override lazy val providers = ListMap(
      include(new EmailPasswordProvider[YetuUser](userService, avatarService, viewTemplates, passwordHashers)),
      include(signatureAuthenticationProvider)
    )

    override lazy val authenticatorService = new AuthenticatorService(
      new CustomCookieAuthenticatorBuilder[YetuUser](cookieAuthStore, idGenerator),
      new HttpHeaderAuthenticatorBuilder[YetuUser](httpAuthStore, idGenerator)
    )
  }

  lazy val setupController = wire[SetupController]
}

object PersistentControllerRegistry extends ControllerRegistry with PersistentDataServices
object MemoryControllerRegistry extends ControllerRegistry with InMemoryDataServices
