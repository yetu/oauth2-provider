package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.data.riak.RiakConnection
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.libs.json.Json
import securesocial.core.services.CacheService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RiakCacheService(riakConnection: RiakConnection) extends CacheService with NamedLogger {

  override def set[T](key: String, value: T, ttlInSeconds: Int): Future[Unit] = {
    riakConnection.mailTokenBucket.store(key, Json.toJson(value.asInstanceOf[YetuUser]).toString())
  }

  override def getAs[T](key: String)(implicit ct: ClassManifest[T]): Future[Option[T]] = {
    riakConnection.mailTokenBucket.fetch(key).map(p => {
      p.map(o => Json.parse(o.data).as[YetuUser].asInstanceOf[T])
    })
  }

  override def remove(key: String): Future[Unit] = {
    riakConnection.mailTokenBucket.delete(key)
  }
}