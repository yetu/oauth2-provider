package com.yetu.oauth2provider
package controllers

import com.yetu.oauth2provider.services.data.interface.IPersonService
import play.api.libs.json.{ Json, JsValue }
import play.api.mvc.{ Result, Action }
import com.yetu.oauth2provider.oauth2.services.ScopeService
import com.yetu.oauth2provider.utils.Config

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

//TODO: Remove this entirely
class OAuth2TrustedServer(scopeService: ScopeService,
    personService: IPersonService) extends OAuth2Controller {

  private val communityServiceSecret = "94363834-3c53-4c4e-b432-d3ebc3874bd2"

  /**
   * This is a temporary solution to validate the working method, this method should be
   * removed or secured in a better way when community services are completed.
   *
   * THIS CODE MUST NOT BE DEPLOYED TO DEMO SERVER !!!!
   * @return
   */
  def getUserProfile = Action.async {
    implicit request =>
      authorizeTrustedServer { userId =>
        {

          personService.findUser(userId).map {
            case Some(id) =>

              val outputJson: Option[JsValue] = scopeService
                .getInfoByScope(id, Config.SCOPE_CONTACT)
                .map(user => Json.toJson(user))

              outputJson match {
                case Some(userJson) => Ok(userJson)
                case None => BadRequest("You have been authorized, but your scope is invalid or " +
                  "not authorized, you cannot access any data for this user.")
              }

            case _ => NotFound("The requested user does not exist")
          }
        }
      }

  }

  private def authorizeTrustedServer[A, U](callback: String => Future[Result])(implicit request: play.api.mvc.Request[A]): Future[Result] = {
    val user = request.getQueryString("user")
    val resourceSecret = request.getQueryString("secret")

    resourceSecret match {
      case Some(communityServiceSecret) => callback(user.get)
      case _                            => Future.successful(Unauthorized)
    }
  }

}
