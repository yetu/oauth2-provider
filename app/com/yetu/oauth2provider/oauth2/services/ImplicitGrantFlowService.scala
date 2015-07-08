package com.yetu.oauth2provider.oauth2.services

import com.yetu.oauth2provider.controllers.authentication.providers.EmailPasswordProvider
import com.yetu.oauth2provider.oauth2.models.{ ImplicitFlowException, ImplicitFlowSyntaxException, YetuUser }
import com.yetu.oauth2provider.services.data.interface.IPersonService
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider._

class ImplicitGrantFlowService[U](personService: IPersonService) {

  lazy val logger = Logger("com.yetu.oauth2provider.oauth2.services.ImplicitGrantFlowService")

  def validateRequest(implicit request: AuthorizationRequest): Future[YetuUser] = {
    parseHeaders { email =>
      personService.findByEmailAndProvider(email, EmailPasswordProvider.EmailPassword).map {
        case Some(user) => user.asInstanceOf[YetuUser]
        case _          => throw new ImplicitFlowException("user not found")
      }
    }
  }

  //TODO change exception class SignatureSyntaxException  to something else
  def parseHeaders(callback: (String) => Future[YetuUser])(implicit request: AuthorizationRequest): Future[YetuUser] = {
    request.headers.get("email").map(_.head) match {
      case Some(e) => callback(e)
      case _       => Future.failed(ImplicitFlowSyntaxException("Missing parameter [ email ]"))
    }
  }

}

