package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.oauth2.models.YetuUser
import play.api.Play._
import play.api.mvc._
import securesocial.controllers._
import securesocial.core._
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.services.RoutesService

class LoginApi(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseLoginApi[YetuUser]

class Registration(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser] {
  val logger = play.api.Logger("com.yetu.oauth2provider.controllers.authentication.Registration")
  logger.error(s"Skip login on signup: ${UsernamePasswordProvider.signupSkipLogin}")
}

class PasswordReset(implicit override val env: RuntimeEnvironment[YetuUser]) extends BasePasswordReset[YetuUser]

class PasswordChange(implicit override val env: RuntimeEnvironment[YetuUser]) extends BasePasswordChange[YetuUser]

class CustomRoutesService extends RoutesService.Default {

  override def loginPageUrl(implicit req: RequestHeader): String = {
    routes.LoginPage.login().absoluteURL(IdentityProvider.sslEnabled)
  }

  override def startSignUpUrl(implicit req: RequestHeader): String = {
    absoluteUrl(routes.Registration.startSignUp())
  }

  override def startResetPasswordUrl(implicit request: RequestHeader): String = {
    absoluteUrl(routes.PasswordReset.startResetPassword())
  }

  override def authenticationUrl(provider: String, redirectTo: Option[String] = None)(implicit req: RequestHeader): String = {
    absoluteUrl(routes.ProviderController.authenticate(provider, redirectTo))
  }
}