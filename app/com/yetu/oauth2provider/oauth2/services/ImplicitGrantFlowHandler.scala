package com.yetu.oauth2provider.oauth2.services

import com.yetu.oauth2provider.oauth2.models.{ ImplicitFlowSyntaxException, ImplicitFlowException }
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scalaoauth2.provider._

class ImplicitGrantFlowHandler[A](implicitGrantFlowService: ImplicitGrantFlowService[A]) extends GrantHandler {

  lazy val logger = Logger("com.yetu.oauth2provider.oauth2.services.ImplicitGrantFlowHandler")

  override def clientCredentialRequired = false

  override def handleRequest[B](request: AuthorizationRequest, maybeClientCredential: Option[ClientCredential], handler: AuthorizationHandler[B]): Future[GrantHandlerResult] = {
    implicitGrantFlowService.validateRequest(request) flatMap { user =>

      val scope = request.scope
      val clientId = maybeClientCredential.map(_.clientId)
      val authInfo = AuthInfo(user, clientId, scope, None)

      val myHandler: AuthorizationHandler[Any] = handler.asInstanceOf[AuthorizationHandler[Any]]
      issueAccessToken(myHandler, authInfo)
    } recover withErrorHandling
  }

  private def withErrorHandling: PartialFunction[Throwable, GrantHandlerResult] = {
    case ImplicitFlowSyntaxException(message) => throw new InvalidRequest(message)
    case ImplicitFlowException(message)       => throw new InvalidGrant(message)
  }

}