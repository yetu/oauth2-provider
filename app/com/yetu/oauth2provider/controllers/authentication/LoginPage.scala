package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import securesocial.controllers.BaseLoginPage
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Future

class LoginPage(authAccessTokenService: IAuthCodeAccessTokenService)(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseLoginPage[YetuUser] {
  override def login = KillOldDomainCookies {
    super.login
  }

  override def logout = DeleteRelevantAccessTokens(authAccessTokenService) {
    //TODO: implement deleting access tokens!
    super.logout
  }
}

import play.api.mvc._

case class KillOldDomainCookies[A](action: Action[A]) extends Action[A] {
  import scala.concurrent.ExecutionContext.Implicits.global
  import securesocial.core.authenticator.CookieAuthenticator._

  //TODO: in a few weeks, remove this cookie busting code.
  val discardingCookie1 = DiscardingCookie(cookieName, cookiePath, Some("auth-dev.yetu.me"), cookieSecure)
  val discardingCookie2 = DiscardingCookie(cookieName, cookiePath, Some("auth-prod.yetu.me"), cookieSecure)
  val discardingCookie3 = DiscardingCookie(cookieName, cookiePath, None, cookieSecure)

  def apply(request: Request[A]): Future[Result] = {
    action(request).map(_.discardingCookies(discardingCookie1, discardingCookie2, discardingCookie3))
  }

  lazy val parser = action.parser
}

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