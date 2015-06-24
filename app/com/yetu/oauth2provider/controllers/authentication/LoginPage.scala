package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.IAuthCodeAccessTokenService
import com.yetu.oauth2provider.utils.StringUtils
import play.api.mvc._
import securesocial.controllers.BaseLoginPage
import securesocial.core.authenticator.CookieAuthenticator
import securesocial.core.RuntimeEnvironment
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class LoginPage(authAccessTokenService: IAuthCodeAccessTokenService)(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseLoginPage[YetuUser] {

  override def login = DiscardingCookieAction {
    super.login()
  }

  case class DiscardingCookieAction[A](action: Action[A]) extends Action[A] {
    def apply(request: Request[A]): Future[Result] = {
      action(request).map(_.discardingCookies(DiscardingCookie(
        CookieAuthenticator.DefaultCookieName,
        "/",
        Some(StringUtils.subdomain(request.host)))))
    }
    lazy val parser = action.parser
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