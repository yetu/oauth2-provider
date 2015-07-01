package com.yetu.oauth2provider.services.data.memory

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ YetuUser, YetuUserHelper }
import com.yetu.oauth2provider.services.data.interface.IPersonService
import org.joda.time.DateTime
import play.api.Logger
import securesocial.core.services.SaveMode
import securesocial.core.{ BasicProfile, PasswordInfo }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MemoryPersonService extends IPersonService {

  import com.yetu.oauth2provider.services.data.memory.MemoryPersonService._

  val logger = Logger("com.yetu.oauth2provider.services.memory.MemoryPersonService")

  def updatePasswordInfo(user: YetuUser, info: PasswordInfo): Future[Option[BasicProfile]] = {

    val update = usersIds.values.find(_.userId == user.userId) match {
      case Some(u) => Some(u.copy(passwordInfo = Some(info)))
      case _       => None
    }

    val profile = if (update.isDefined) {

      usersIds += update.get.userId -> update.get
      usersEmails += update.get.email -> update.get
      update.map(_.toBasicProfile)

    } else None

    Future.successful(profile)
  }

  def passwordInfoFor(user: YetuUser): Future[Option[PasswordInfo]] = {
    Future.successful(usersIds.values.find(_ == user).map(u => u.passwordInfo.get))
  }

  def find(providerId: String, userId: String) = {
    findUser(userId).map {

      case Some(user) =>
        if (user.providerId.equals(providerId)) {
          Some(user.toBasicProfile)
        } else None

      case _ => None
    }
  }

  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {

    val user = usersEmails.find(_._1 == email).map(_._2)
    val result = user match {

      case Some(u) =>
        if (u.providerId.equals(providerId)) {
          Some(u.toBasicProfile)
        } else None

      case _ => None
    }

    Future.successful(result)
  }

  override def save(user: BasicProfile, mode: SaveMode) = {
    logger.debug(s"Save user $user")
    val result = mode match {
      case SaveMode.SignUp => addNewUser(YetuUserHelper.fromBasicProfile(user))
      case SaveMode.PasswordChange => {

        for {
          oldUser <- findUser(user.userId).map {
            case Some(u) => usersIds += user.userId -> u.copy(passwordInfo = user.passwordInfo)
            case _       => Unit
          }
          find <- findUser(user.userId)
        } yield find

      }
      case _ =>
        logger.warn("not saving as signUp; ignoring request.")
        findUser(user.userId)
    }

    result.map(_.orNull)
  }

  def addNewUser(user: YetuUser) = {
    val userWithRegistrationDate = user.copy(registrationDate = Some(DateTime.now()))

    usersIds += (user.userId -> userWithRegistrationDate)
    usersEmails += (user.email -> userWithRegistrationDate)

    Future.successful(usersIds.get(user.userId))
  }

  override def link(current: YetuUser, to: BasicProfile): Future[YetuUser] = {
    Future.successful(current)
  }

  override def updateUserProfile(yetuUser: YetuUser, request: DataUpdateRequest) = {

    val user = yetuUser.copy(
      contactInfo = request.contactInfo,
      firstName = request.firstName.get,
      lastName = request.lastName.get)

    Future.successful {
      usersIds += yetuUser.userId -> user
      usersEmails += yetuUser.email -> user
    }
  }

  override def deleteUser(email: String) = {
    Future.successful(usersIds -= email)
  }

  override def findUser(userId: String) = {
    Future.successful(usersIds.find(_._1 == userId).map(_._2))
  }

}

object MemoryPersonService {
  var usersIds = Map[String, YetuUser]()
  var usersEmails = Map[String, YetuUser]()
}
