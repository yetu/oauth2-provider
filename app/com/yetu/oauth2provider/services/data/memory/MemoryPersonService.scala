package com.yetu.oauth2provider.services.data.memory

import java.util.Date

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ YetuUser, YetuUserHelper }
import com.yetu.oauth2provider.services.data.interface.IPersonService
import com.yetu.oauth2provider.utils.UUIDGenerator
import play.api.Logger
import securesocial.core.services.SaveMode
import securesocial.core.{ BasicProfile, PasswordInfo }

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class MemoryPersonService extends IPersonService {

  import com.yetu.oauth2provider.services.data.memory.MemoryPersonService.users

  val logger = Logger("com.yetu.oauth2provider.services.memory.MemoryPersonService")

  def updatePasswordInfo(user: YetuUser, info: PasswordInfo): Future[Option[BasicProfile]] = {
    Future.successful(Some(user.toBasicProfile))
  }

  def passwordInfoFor(user: YetuUser): Future[Option[PasswordInfo]] = {
    Future.successful(users.values.find(_ == user).map(u => u.passwordInfo.get))
  }

  def find(providerId: String, userId: String) = {
    findYetuUser(userId).map {

      case Some(user) =>
        if (user.providerId.equals(providerId)) {
          Some(user.toBasicProfile)
        } else None

      case _ => None
    }
  }

  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    find(providerId, email)
  }

  override def save(user: BasicProfile, mode: SaveMode) = {
    logger.debug(s"Save user $user")
    val result = mode match {
      case SaveMode.SignUp => {
        val newUser = YetuUserHelper.fromBasicProfile(user, UUIDGenerator.uuid())
        logger.debug(s"saving user $newUser")
        addNewUser(newUser)
      }
      case SaveMode.PasswordChange => {

        for {
          oldUser <- findYetuUser(user.userId).map {
            case Some(u) => users += user.userId -> u.copy(passwordInfo = user.passwordInfo)
            case _       => Unit
          }
          find <- findYetuUser(user.userId)
        } yield find

      }
      case _ =>
        logger.warn("not saving as signUp; ignoring request.")
        findYetuUser(user.userId)
    }

    result.map(_.orNull)
  }

  def addNewUser(user: YetuUser) = {
    val userWithRegistrationDate = user.copy(registrationDate = Some(new Date()))
    users = users + (user.userId -> userWithRegistrationDate)
    Future.successful(users.get(user.userId))
  }

  override def link(current: YetuUser, to: BasicProfile): Future[YetuUser] = {
    Future.successful(current)
  }

  override def updateUserProfile(yetuUser: YetuUser, request: DataUpdateRequest) = {
    Future.successful(
      users += yetuUser.userId -> yetuUser.copy(
        contactInfo = request.contactInfo,
        firstName = request.firstName.get,
        lastName = request.lastName.get))
  }

  override def deleteUser(email: String) = {
    Future.successful(users -= email)
  }

  override def findYetuUser(userId: String) = {
    Future.successful(users.find(_._1 == userId).map(_._2))
  }

}

object MemoryPersonService {
  var users = Map[String, YetuUser]()
}
