package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.controllers.setup
import com.yetu.oauth2provider.oauth2.models.YetuUser
import play.api.mvc._
import securesocial.controllers._
import securesocial.core._
import securesocial.core.services.RoutesService

class LoginApi(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseLoginApi[YetuUser]

class Registration(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser]

class PasswordReset(implicit override val env: RuntimeEnvironment[YetuUser]) extends BasePasswordReset[YetuUser]

class PasswordChange(implicit override val env: RuntimeEnvironment[YetuUser]) extends BasePasswordChange[YetuUser]

class CustomRoutesService extends RoutesService.Default {

  override def loginPageUrl(implicit req: RequestHeader): String = {
    routes.LoginPage.login().absoluteURL(IdentityProvider.sslEnabled)
  }

  override def startSignUpUrl(implicit req: RequestHeader): String = {
    absoluteUrl(setup.routes.SetupController.startSignUp())
  }

  override def startResetPasswordUrl(implicit request: RequestHeader): String = {
    absoluteUrl(routes.PasswordReset.startResetPassword())
  }

  override def authenticationUrl(provider: String, redirectTo: Option[String] = None)(implicit req: RequestHeader): String = {
    absoluteUrl(routes.ProviderController.authenticate(provider, redirectTo))
  }
}