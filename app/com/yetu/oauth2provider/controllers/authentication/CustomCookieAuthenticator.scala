package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.utils.Config.SessionStatusCookie
import org.joda.time.DateTime
import play.api.mvc._
import securesocial.core.authenticator._

import scala.concurrent.Future

/**
 * A modified copy of the Cookie Authenticator from securesocial.  All additional/changed code is tagged |-EXTRA-|,
 * and it was renamed to CustomCookieAuthenticator from CookieAuthenticator
 *
 * See original code for details.
 */
case class CustomCookieAuthenticator[U](id: String, user: U, expirationDate: DateTime,
    lastUsed: DateTime,
    creationDate: DateTime,
    @transient store: AuthenticatorStore[CustomCookieAuthenticator[U]]) extends StoreBackedAuthenticator[U, CustomCookieAuthenticator[U]] {
  @transient
  override val idleTimeoutInMinutes = CookieAuthenticator.idleTimeout

  @transient
  override val absoluteTimeoutInSeconds = CookieAuthenticator.absoluteTimeoutInSeconds

  def withLastUsedTime(time: DateTime): CustomCookieAuthenticator[U] = this.copy[U](lastUsed = time)

  def withUser(user: U): CustomCookieAuthenticator[U] = this.copy[U](user = user)

  override def discarding(result: Result): Future[Result] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    store.delete(id).map { _ =>
      result.discardingCookies(CookieAuthenticator.discardingCookie,
        SessionStatusCookie.discardingCookie // |-EXTRA-|
      )
    }
  }

  override def starting(result: Result): Future[Result] = {
    Future.successful {
      result.withCookies(
        Cookie(
          CookieAuthenticator.cookieName,
          id,
          if (CookieAuthenticator.makeTransient)
            CookieAuthenticator.Transient
          else Some(CookieAuthenticator.absoluteTimeoutInSeconds),
          CookieAuthenticator.cookiePath,
          CookieAuthenticator.cookieDomain,
          secure = CookieAuthenticator.cookieSecure,
          httpOnly = CookieAuthenticator.cookieHttpOnly
        )
      )
    }
  }

  override def discarding(javaContext: play.mvc.Http.Context): Future[Unit] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    store.delete(id).map { _ =>
      javaContext.response().discardCookie(
        CookieAuthenticator.cookieName,
        CookieAuthenticator.cookiePath,
        CookieAuthenticator.cookieDomain.getOrElse(null),
        CookieAuthenticator.cookieSecure
      )
    }
  }
}

/**
 * A modified copy of the Cookie Authenticator builder from securesocial.
 */
class CustomCookieAuthenticatorBuilder[YetuUser](store: AuthenticatorStore[CustomCookieAuthenticator[YetuUser]], generator: IdGenerator) extends AuthenticatorBuilder[YetuUser] {

  val id = CookieAuthenticator.Id

  /**
   * Creates an instance of a CookieAuthenticator from the http request
   *
   * @param request the incoming request
   * @return an optional CookieAuthenticator instance.
   */
  override def fromRequest(request: RequestHeader): Future[Option[CustomCookieAuthenticator[YetuUser]]] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    request.cookies.get(CookieAuthenticator.cookieName) match {
      case Some(cookie) => store.find(cookie.value).map { retrieved =>
        retrieved.map {
          _.copy(store = store)

        }
      }
      case None => Future.successful(None)
    }
  }

  /**
   * Creates an instance of a CookieAuthenticator from a user object.
   *
   * @param user the user
   * @return a CookieAuthenticator instance.
   */
  override def fromUser(user: YetuUser): Future[CustomCookieAuthenticator[YetuUser]] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    generator.generate.flatMap {
      id =>
        val now = DateTime.now()
        val expirationDate = now.plusMinutes(CookieAuthenticator.absoluteTimeout)
        val authenticator = CustomCookieAuthenticator(id, user, expirationDate, now, now, store)
        store.save(authenticator, CookieAuthenticator.absoluteTimeoutInSeconds)
    }
  }
}