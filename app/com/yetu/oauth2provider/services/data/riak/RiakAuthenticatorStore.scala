package com.yetu.oauth2provider.services.data.riak

import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.NamedLogger
import org.joda.time.DateTime
import play.api.libs.json.{ Format, Json }
import securesocial.core.authenticator.{ Authenticator, AuthenticatorStore }

import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.ClassTag

class RiakAuthenticatorStore[A <: Authenticator[_]](riakConnection: RiakConnection)(implicit executionContext: ExecutionContext)
    extends AuthenticatorStore[A] with NamedLogger {

  /**
   * Retrieves an Authenticator from the cache
   *
   * @param id the authenticator id
   * @param ct the class tag for the Authenticator type
   * @return an optional future Authenticator
   */
  override def find(id: String)(implicit ct: ClassTag[A]): Future[Option[A]] = {
    riakConnection.sessionBucket.fetch(id).map(p => {
      p.map(o => Json.parse(o.data).as[RiakAuthenticator]).map(riakAuthenticator => {

        val args = Array[AnyRef](
          riakAuthenticator.id,
          riakAuthenticator.user,
          riakAuthenticator.expirationDate,
          riakAuthenticator.lastUsed,
          riakAuthenticator.creationDate,
          this)

        ct.runtimeClass.getDeclaredConstructors()(0).newInstance(args: _*).asInstanceOf[A]
      })
    })
  }

  /**
   * Saves/updates an authenticator into the cache
   *
   * @param authenticator the istance to save
   * @param timeoutInSeconds the timeout.
   * @return the saved authenticator
   */
  override def save(authenticator: A, timeoutInSeconds: Int): Future[A] = {

    val riakAuthenticator = RiakAuthenticator(
      authenticator.id,
      authenticator.user.asInstanceOf[YetuUser],
      authenticator.creationDate,
      authenticator.lastUsed,
      authenticator.expirationDate)

    riakConnection.sessionBucket
      .storeAndFetch(authenticator.id, Json.toJson(riakAuthenticator).toString())
      .map(a => authenticator)
  }

  /**
   * Deletes an Authenticator from the cache
   *
   * @param id the authenticator id
   * @return a future of Unit
   */
  override def delete(id: String): Future[Unit] = {
    riakConnection.sessionBucket.delete(id)
  }
}

case class RiakAuthenticator(id: String,
  user: YetuUser,
  creationDate: DateTime,
  lastUsed: DateTime,
  expirationDate: DateTime)

object RiakAuthenticator {
  implicit val jsonFormat: Format[RiakAuthenticator] = Json.format[RiakAuthenticator]
}