package com.yetu.oauth2provider.services.data.memory

import java.util.Date

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ YetuUser, YetuUserHelper }
import com.yetu.oauth2provider.services.data.interface.IPersonService
import com.yetu.oauth2provider.utils.UUIDGenerator
import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results._
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.services.SaveMode
import securesocial.core.{ BasicProfile, PasswordInfo }

import scala.concurrent.Future

class MemoryPersonService extends IPersonService {

  import com.yetu.oauth2provider.services.data.memory.MemoryPersonService.users

  val logger = Logger("com.yetu.oauth2provider.services.memory.MemoryPersonService")

  def updatePasswordInfo(user: YetuUser, info: PasswordInfo): Future[Option[BasicProfile]] = {
    Future.successful {
      Some(user.main)
    }
  }

  def passwordInfoFor(user: YetuUser): Future[Option[PasswordInfo]] = {
    Future.successful {
      for (
        found <- users.values.find(_ == user);
        identityWithPasswordInfo <- found.identities.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
      ) yield {
        identityWithPasswordInfo.passwordInfo.get
      }
    }
  }

  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    Future.successful(findYetuUser(userId).map(_.toBasicProfile))
  }

  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    find(providerId, email)
  }

  def save(user: BasicProfile, mode: SaveMode): Future[YetuUser] = {
    logger.debug(s"Save user $user")
    Future.successful {
      val userToReturn: YetuUser = mode match {
        case SaveMode.SignUp => {
          val newUser = YetuUserHelper.fromBasicProfile(user, UUIDGenerator.uuid())
          logger.debug(s"saving user $newUser")
          addNewUser(newUser)
          newUser
        }
        case SaveMode.PasswordChange => {
          val oldUser = findYetuUser(user.userId).get
          val newUser = oldUser.copy(passwordInfo = user.passwordInfo)
          users += user.userId -> newUser
          findYetuUser(user.userId).get
        }
        case _ =>
          logger.warn("not saving as signUp; ignoring request.")
          findYetuUser(user.userId).get
      }
      userToReturn
    }
  }

  def addNewUser(user: YetuUser) = {
    val userWithRegistrationDate = user.copy(registrationDate = Some(new Date()))
    users = users + (user.userId -> userWithRegistrationDate)
    user
  }

  def link(current: YetuUser, to: BasicProfile): Future[YetuUser] = {
    Future.successful(current)
  }

  override def updateUserProfile(yetuUser: YetuUser, request: DataUpdateRequest): Result = {
    users += yetuUser.userId -> yetuUser.copy(contactInfo = request.contactInfo, firstName = request.firstName.get, lastName = request.lastName.get)
    println(s"USERS: $users")
    //TODO: should not return a Result type in the service!
    NoContent
  }

  override def deleteUser(email: String): Unit = {
    users -= email
  }

  override def findYetuUser(userId: String): Option[YetuUser] = {
    val found = users.find(_._1 == userId).map(_._2)
    found
  }

}

object MemoryPersonService {

  var users = Map[String, YetuUser]()
}
