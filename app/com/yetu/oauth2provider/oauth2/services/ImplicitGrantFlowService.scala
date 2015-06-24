package com.yetu.oauth2provider.oauth2.services

import com.yetu.oauth2provider.oauth2.models.{ ImplicitFlowSyntaxException, ImplicitFlowException, YetuUser }
import com.yetu.oauth2provider.services.data.interface.IPersonService
import play.api.Logger

import scala.concurrent.Future
import scalaoauth2.provider._

class ImplicitGrantFlowService[U](personService: IPersonService) {

  lazy val logger = Logger("com.yetu.oauth2provider.oauth2.services.ImplicitGrantFlowService")

  def validateRequest(implicit request: AuthorizationRequest): Future[YetuUser] = {
    parseHeaders { email =>

      val maybeUser = personService.findYetuUser(email)

      val maybeSuccess = for {
        user <- maybeUser
      } yield Future.successful(user)

      maybeSuccess.getOrElse{
        logger.debug(s"could not find the user with the email address [${email}] ")
        Future.failed(ImplicitFlowException("User or key does not exist"))
      }

    }
  }

  //TODO change exception class SignatureSyntaxException  to something else
  def parseHeaders(callback: (String) => Future[YetuUser])(implicit request: AuthorizationRequest): Future[YetuUser] = {
    val email = request.headers.get("email").headOption.map(_.head)

    email match {
      case Some(email) => {
        callback(email)
      }
      case _ => Future.failed(ImplicitFlowSyntaxException("Missing parameter [ email ]"))
    }
  }

}

