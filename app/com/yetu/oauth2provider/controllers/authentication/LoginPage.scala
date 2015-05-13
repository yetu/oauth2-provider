package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.IAuthCodeAccessTokenService
import securesocial.controllers.BaseLoginPage
import securesocial.core.RuntimeEnvironment

import scala.concurrent.Future

class LoginPage(authAccessTokenService: IAuthCodeAccessTokenService)(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseLoginPage[YetuUser] {

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