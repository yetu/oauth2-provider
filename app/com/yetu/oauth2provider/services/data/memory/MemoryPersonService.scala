package com.yetu.oauth2provider.services.data.memory

import com.yetu.oauth2provider.oauth2.models.YetuUser
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

    val updatedUser = usersIds.values.find(_.userId == user.userId) match {
      case Some(u) => Some(u.copy(passwordInfo = Some(info)))
      case _       => None
    }

    val profile = if (updatedUser.isDefined) {

      usersIds += updatedUser.get.userId -> updatedUser.get.asInstanceOf[YetuUser]
      usersEmails += updatedUser.get.email.get -> updatedUser.get.asInstanceOf[YetuUser]

      updatedUser

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
          Some(user)
        } else None

      case _ => None
    }
  }

  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {

    val user = usersEmails.find(_._1 == email).map(_._2)
    val result = user match {

      case Some(u) =>
        if (u.providerId.equals(providerId)) {
          Some(u)
        } else None

      case _ => None
    }

    Future.successful(result)
  }

  override def save(user: BasicProfile, mode: SaveMode) = {
    logger.debug(s"Save user $user")
    val result = mode match {
      case SaveMode.SignUp => addUser(user.asInstanceOf[YetuUser])
      case SaveMode.PasswordChange => {

        for {
          oldUser <- findUser(user.userId).map {
            case Some(u) => {
              val modifiedUser = u.copyUser(passwordInfo = user.passwordInfo)
              usersIds += user.userId -> modifiedUser
            }
            case _ => Unit
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

  def addUser(user: YetuUser) = {
    user.registrationDate = Some(DateTime.now())

    usersIds += (user.userId -> user)
    usersEmails += (user.email.get -> user)

    Future.successful(usersIds.get(user.userId))
  }

  override def link(current: YetuUser, to: BasicProfile): Future[YetuUser] = {
    Future.successful(current)
  }

  override def updateUser(user: YetuUser) = {
    usersIds += user.userId -> user
    usersEmails += user.email.get -> user

    Future.successful(Some(user))
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
