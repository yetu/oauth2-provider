package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import com.yetu.oauth2provider.utils.StringUtils
import play.api.mvc.DiscardingCookie
import securesocial.controllers.{ ProviderControllerHelper, BaseLoginPage }
import securesocial.core.authenticator.CookieAuthenticator
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.{ SecureSocial, RuntimeEnvironment }

import scala.concurrent.Future

class LoginPage(authAccessTokenService: IAuthCodeAccessTokenService)(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseLoginPage[YetuUser] {

  override def login = {
    UserAwareAction { implicit request =>

      var result = Redirect(ProviderControllerHelper.landingUrl)
      if (!request.user.isDefined) {

        result = Ok(env.viewTemplates.getLoginPage(UsernamePasswordProvider.loginForm))
        if (SecureSocial.enableRefererAsOriginalUrl) {

          result = SecureSocial.withRefererAsOriginalUrl(
            Ok(env.viewTemplates.getLoginPage(UsernamePasswordProvider.loginForm)))
        }
      }

      // Avoid domain cookie interpolation
      result.discardingCookies(DiscardingCookie(
        CookieAuthenticator.DefaultCookieName,
        "/",
        Some(StringUtils.subdomain(request.host))))
    }
  }

}

import play.api.mvc._

case class DeleteRelevantAccessTokens[A](authAccessTokenService: IAuthCodeAccessTokenService)(action: Action[A]) extends Action[A] {
  //TODO: implement this! Perhaps (? if possible since user is needed ?) use action composition as described
  // here: https://www.playframework.com/documentation/2.3.x/ScalaActionsComposition
  //
  //authAccessTokenService.deleteAll(identity)

  def apply(request: Request[A]): Future[Result] = {
    action(request)
  }

  lazy val parser = action.parser
}