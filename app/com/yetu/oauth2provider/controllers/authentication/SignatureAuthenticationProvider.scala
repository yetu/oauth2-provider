package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.signature.models.{ SignatureException, SignatureSyntaxException }
import com.yetu.oauth2provider.signature.services.SignatureService
import play.api.Logger
import play.api.mvc._
import securesocial.core._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider.OAuth2BaseProvider

class SignatureAuthenticationProvider[U](signatureService: SignatureService[U]) extends IdentityProvider with ApiSupport with OAuth2BaseProvider {

  lazy val logger = Logger("com.yetu.oauth2provider.controllers.authentication.SignatureAuthenticationProvider")

  override val id: String = SignatureAuthenticationProvider.SignatureAuthentication

  def authMethod: AuthenticationMethod = AuthenticationMethod(id)

  def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult] = doAuthentication()

  private def doAuthentication[A](apiMode: Boolean = false)(implicit request: Request[A]): Future[AuthenticationResult] = {
    signatureService.validateRequest(request).map {
      case Some(user) => AuthenticationResult.Authenticated(user.toBasicProfile)
      case _          => AuthenticationResult.AccessDenied()
    } recover withErrorHandling
  }

  private def withErrorHandling: PartialFunction[Throwable, AuthenticationResult] = {
    case SignatureSyntaxException(message) => AuthenticationResult.NavigationFlow(BadRequest(message))
    case SignatureException(message)       => AuthenticationResult.NavigationFlow(Unauthorized(message))
  }

  def authenticateForApi(implicit request: Request[AnyContent]): Future[AuthenticationResult] = ???

}

object SignatureAuthenticationProvider {
  val SignatureAuthentication = "SignatureAuthentication"
}

