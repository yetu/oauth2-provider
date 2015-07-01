package com.yetu.oauth2provider.services.data.api

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ YetuUser, YetuUserHelper }
import com.yetu.oauth2provider.services.data.interface.IPersonService
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.Play.current
import play.api.libs.ws._
import play.mvc.Http
import securesocial.core.services.SaveMode
import securesocial.core.{ BasicProfile, PasswordInfo }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class APIPersonService extends IPersonService with APIHelper with NamedLogger {

  private def changePassword(profile: BasicProfile) = ???

  override def updateUserProfile(user: YetuUser, request: DataUpdateRequest) = {
    WS.url(urlForResource("users", user.userId, Version1)).post(YetuUserHelper.toJson(user)).map(_ => Some(user))
  }

  override def deleteUser(id: String) = {
    WS.url(urlForResource("users", id, Version1)).delete().map(_ => Unit)
  }

  override def findUser(userId: String) = {
    WS.url(urlForResource("users", userId, Version1)).get().map(response => {
      if (response.status == Http.Status.OK) {

        Some(YetuUserHelper.fromJson(response.body))

      } else None
    })
  }

  override def addNewUser(user: YetuUser) = {
    WS.url(url("users", Version1)).post(YetuUserHelper.toJson(user)).map(_ => Some(user))
  }

  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    WS.url(urlForResource("users/email", email, Version1)).get().map(response => {
      if (response.status == Http.Status.OK) {

        val user = YetuUserHelper.fromJson(response.body)
        if (user.providerId.equals(providerId)) {

          Some(user.toBasicProfile)

        } else None
      } else None
    })
  }

  override def save(profile: BasicProfile, mode: SaveMode) = {
    val result = mode match {
      case SaveMode.LoggedIn       => findUser(profile.userId)
      case SaveMode.PasswordChange => changePassword(profile)
      case SaveMode.SignUp         => addNewUser(YetuUserHelper.fromBasicProfile(profile))
    }

    result.map(_.orNull)
  }

  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    findUser(userId).map {

      case Some(user) =>
        if (user.providerId.eq(providerId)) {
          Some(user.toBasicProfile)
        } else None

      case _ => None
    }
  }

  /**
   * Links the current user to another profile
   *
   * @param current The current user instance
   * @param to the profile that needs to be linked to
   */
  override def link(current: YetuUser, to: BasicProfile): Future[YetuUser] = ???

  /**
   * Returns an optional PasswordInfo instance for a given user
   *
   * @param user a user instance
   * @return returns an optional PasswordInfo
   */
  override def passwordInfoFor(user: YetuUser): Future[Option[PasswordInfo]] = ???

  /**
   * Updates the PasswordInfo for a given user
   *
   * @param user a user instance
   * @param info the password info
   * @return
   */
  override def updatePasswordInfo(user: YetuUser, info: PasswordInfo): Future[Option[BasicProfile]] = ???
}
