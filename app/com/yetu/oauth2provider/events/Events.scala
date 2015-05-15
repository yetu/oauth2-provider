package com.yetu.oauth2provider.events

import com.yetu.notification.client.NotificationManager
import com.yetu.notification.client.model.RabbitMessage
import com.yetu.oauth2provider.oauth2.handlers.AuthorizationHandler
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.Config.YetuMessageEvents
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.{ Application, Logger }
import play.api.libs.json._
import play.api.mvc.{ RequestHeader, Session }
import securesocial.core.{ Event, EventListener, LogoutEvent }

// TODO replace it with reading information form the RabbitMessage header
// in the yetu-notification-client library
case class EventLogoutMessage(event: String, userId: String)

object EventLogoutMessage {
  implicit val eventLogoutMessage = Json.format[EventLogoutMessage]
}

class LogoutEventListener(app: Application, authorizationHandler: AuthorizationHandler) extends EventListener[YetuUser] with NamedLogger {

  override def onEvent(event: Event[YetuUser], request: RequestHeader, session: Session): Option[Session] = {
    event match {
      case LogoutEvent(user) =>
        broadcastLogout(user)
      case otherEvent =>
        Logger.trace("traced %s event for user %s".format(otherEvent, event.user))
    }
    None
  }

  def broadcastLogout(user: YetuUser) = {
    val LOGOUT_EVENT_NAME = YetuMessageEvents.logoutEventName
    val currentClientId = YetuMessageEvents.clientId

    // TODO please change it and take information from topic into account
    NotificationManager.publisherActor ! RabbitMessage(s"${user.uid}.$currentClientId.$LOGOUT_EVENT_NAME",
      Json.toJson(EventLogoutMessage(LOGOUT_EVENT_NAME, user.uid)).toString()
    )
  }
}
