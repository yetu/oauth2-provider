package com.yetu.oauth2provider.events

import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.Application
import play.api.Logger
import play.api.libs.json.JsNull
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.libs.ws.WSResponse
import play.api.mvc.RequestHeader
import play.api.mvc.Session
import securesocial.core.Event
import securesocial.core.EventListener
import securesocial.core.LogoutEvent

import scala.concurrent.Future
import scalaoauth2.provider.AuthInfo
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

case class Payload(event: String, data: JsValue)

object Payload {
  implicit val payloadJsonFormat = Json.format[Payload]
}

case class ClientMessage(token: String, payload: Payload)

object ClientMessage {
  implicit val messageJsonFormat = Json.format[ClientMessage]
}

class LogoutEventListener(app: Application, authorizationHandler: AuthorizationHandler) extends EventListener[YetuUser] with NamedLogger {

  override def onEvent(event: Event[YetuUser], request: RequestHeader, session: Session): Option[Session] = {
    event match {
      case LogoutEvent(user) => {
        broadcastLogout(user) map { response =>
          val logMessage = s"Logout event broadcast for ${event.user.uid} "
          response.status match {
            case ACCEPTED => logger.debug(logMessage)
            case statusCode => { //unexpected case
              logger.warn(s"User will not have been logged out correctly from all systems! \n" +
                s"$logMessage response : status=$statusCode header = ${response.allHeaders} body = ${response.body}")
            }
          }
        }
      }
      case otherEvent => {
        Logger.trace("traced %s event for user %s".format(otherEvent, event.user))
      }
    }
    None
  }

  def broadcastLogout(user: YetuUser): Future[WSResponse] = {
    //TODO: use config
    val clientId = "com.yetu.oauth2provider"
    val scope = "events"
    val event = "logout"
    val inboxUrl = "http://inbox.yetu.me/publish"

    def createMessage(token: String) = {
      val message = Json.toJson(ClientMessage(token, Payload(event, Json parse """{}""")))
      logger.warn(s"message =${Json.prettyPrint(message)}")
      message
    }

    val authInfo: AuthInfo[YetuUser] = AuthInfo(user, Some(clientId), Some(scope), None)
    for {
      token <- authorizationHandler.createAccessToken(authInfo)
      response <- WS.url(inboxUrl).post(createMessage(token.token))
    } yield response

  }
}
