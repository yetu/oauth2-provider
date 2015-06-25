package com.yetu.oauth2provider.controllers.authentication.providers

import com.yetu.oauth2provider.signature.models.{ SignatureException, SignatureSyntaxException }
import com.yetu.oauth2provider.signature.services.SignatureService
import play.api.mvc._
import securesocial.core._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider.OAuth2BaseProvider

class SignatureAuthenticationProvider[U](signatureService: SignatureService[U]) extends IdentityProvider with ApiSupport with OAuth2BaseProvider {

  override val id: String = SignatureAuthenticationProvider.SignatureAuthentication

  def authMethod: AuthenticationMethod = AuthenticationMethod(id)

  def authenticateForApi(implicit request: Request[AnyContent]): Future[AuthenticationResult] = doAuthentication(apiMode = true)

  def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult] = doAuthentication()

  private def doAuthentication[A](apiMode: Boolean = false)(implicit request: Request[A]): Future[AuthenticationResult] = {
    signatureService.validateRequest(request).map(u => AuthenticationResult.Authenticated(u.toBasicProfile)).recover(withErrorHandling)
  }

  private def withErrorHandling: PartialFunction[Throwable, AuthenticationResult] = {
    case SignatureSyntaxException(message) => AuthenticationResult.NavigationFlow(BadRequest(message))
    case SignatureException(message)       => AuthenticationResult.NavigationFlow(Unauthorized(message))
  }

}

object SignatureAuthenticationProvider {
  val SignatureAuthentication = "SignatureAuthentication"
}