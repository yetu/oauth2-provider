package com.yetu.oauth2provider.oauth2.services

import com.yetu.oauth2provider.signature.models.{ SignatureException, SignatureSyntaxException }
import com.yetu.oauth2provider.signature.services.SignatureService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider._

class SignatureHandler[A](signatureService: SignatureService[A]) extends GrantHandler {

  override def clientCredentialRequired = false

  override def handleRequest[B](request: AuthorizationRequest, maybeClientCredential: Option[ClientCredential], handler: AuthorizationHandler[B]): Future[GrantHandlerResult] = {

    signatureService.validateRequest(request) flatMap { user =>

      val scope = request.scope
      val clientId = maybeClientCredential.map(_.clientId)
      val authInfo = AuthInfo(user, clientId, scope, None)

      //TODO: class cast is not good practice, deeper changes necessary in library?
      val myHandler: AuthorizationHandler[Any] = handler.asInstanceOf[AuthorizationHandler[Any]]
      issueAccessToken(myHandler, authInfo)
    } recover withErrorHandling
  }

  private def withErrorHandling: PartialFunction[Throwable, GrantHandlerResult] = {
    case SignatureSyntaxException(message) => throw new InvalidRequest(message)
    case SignatureException(message)       => throw new InvalidGrant(message)
  }

}