package com.yetu.oauth2provider.controllers.authentication

import securesocial.core.providers
import java.util
import java.util.Date
import com.yetu.oauth2provider.signature.SignatureHelper
import com.yetu.oauth2provider.signature.models.{ SignatureSyntaxException, SignatureException, SignedRequestHeaders, YetuPublicKey }

import com.yetu.oauth2provider.services.data.interface.{ IPublicKeyService, IPersonService }
import com.yetu.oauth2provider.signature.services.SignatureService
import com.yetu.oauth2provider.utils.DateUtility
import net.adamcin.httpsig.api.{ Authorization, _ }
import net.adamcin.httpsig.ssh.jce.{ AuthorizedKeys, UserKeysFingerprintKeyId }
import play.api.Play.current
import play.api.mvc.Results._
import play.api.mvc._
import play.api.{ Logger, Play }
import securesocial.core.AuthenticationResult.Authenticated
import securesocial.core._
import securesocial.core.services.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider.OAuth2BaseProvider

class SignatureAuthenticationProvider[U](signatureService: SignatureService[U]) extends IdentityProvider with ApiSupport with OAuth2BaseProvider {

  lazy val logger = Logger("com.yetu.oauth2provider.controllers.authentication.SignatureAuthenticationProvider")

  override val id: String = SignatureAuthenticationProvider.SignatureAuthentication

  def authMethod: AuthenticationMethod = AuthenticationMethod(id)

  def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult] = doAuthentication()

  private def doAuthentication[A](apiMode: Boolean = false)(implicit request: Request[A]): Future[AuthenticationResult] = {
    signatureService.validateRequest(request) map {
      user => AuthenticationResult.Authenticated(user.toBasicProfile)
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

