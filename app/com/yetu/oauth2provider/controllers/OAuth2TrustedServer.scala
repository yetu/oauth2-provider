package com.yetu.oauth2provider
package controllers

import com.yetu.oauth2provider.services.data.iface.IPersonService
import play.api.libs.json.{ Json, JsValue }
import play.api.mvc.{ Result, Action }
import com.yetu.oauth2provider.oauth2.services.ScopeService
import com.yetu.oauth2provider.utils.Config

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
  def getUserProfile = Action {
    implicit request =>
      authorizeTrustedServer {
        userId =>
          {
            val identity = personService.findYetuUser(userId)
            identity match {
              case Some(id) => {
                val outputJson: Option[JsValue] = scopeService.getInfoByScope(id, Config.SCOPE_CONTACT).map(user => Json.toJson(user))
                outputJson match {
                  case None           => BadRequest("You have been authorized, but your scope is invalid or not authorized, you cannot access any data for this user.")
                  case Some(userJson) => Ok(userJson)
                }
              }
              case _ => NotFound("The requested user does not exist")
            }
          }
      }

  }

  private def authorizeTrustedServer[A, U](callback: String => Result)(implicit request: play.api.mvc.Request[A]): Result = {
    val user = request.getQueryString("user")
    val resourceSecret = request.getQueryString("secret")

    resourceSecret match {
      case Some(communityServiceSecret) => {
        callback(user.get)
      }
      case _ => Unauthorized("")
    }
  }

}
