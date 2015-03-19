package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ YetuUserHelper, IdentityId, YetuUser }
import com.yetu.oauth2provider.services.data.iface.{ IPersonService, IPermissionService }
import com.yetu.oauth2provider.utils.UUIDGenerator
import play.api.Logger
import play.api.mvc.Result
import play.api.mvc.Results._
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.services.SaveMode
import securesocial.core.{ AuthenticationMethod, BasicProfile, PasswordInfo }

import scala.concurrent.Future

/**
 * TODO: implement user service properly!
 */
class MemoryPersonService extends IPersonService {

  import MemoryPersonService.users

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
    logger.info("Saving user starts")
    Future.successful {
      val userToReturn: YetuUser = mode match {
        case SaveMode.SignUp => {
          val newUser = YetuUserHelper.fromBasicProfile(user, UUIDGenerator.uuid())
          logger.debug(s"saving user $newUser")
          addNewUser(newUser)
          newUser
        }
        case _ =>
          logger.warn("not saving as signUp; ignoring request.")
          //          val testUser = YetuUser(IdentityId("test@test.test222", "userpass"), "5d64e6dc-aaaa-4e91-b463-d15qweq25daf95","firstname", "lastname", "firstname lastname as fullname", Some("test@test.test222"), None, AuthenticationMethod("userPassword"), None, None, Some(PasswordInfo("bcrypt", "$2a$10$qHwUqmHA7.24IZFNL90ke.mvjXwznoBh1pGR8D5r1TJ1tf9vttLji", None)))
          findYetuUser(user.userId).get
      }
      userToReturn
    }
  }
  def addNewUser(user: YetuUser) = {
    users = users + (user.userId -> user)
    user
  }

  def link(current: YetuUser, to: BasicProfile): Future[YetuUser] = {
    //    if (current.identities.exists(i => i.providerId == to.providerId && i.userId == to.userId)) {
    //      Future.successful(current)
    //    } else {
    //      val added = to :: current.identities
    //      val updatedUser = current.copy(identities = added)
    //      users = users + ((current.main.providerId, current.main.userId) -> updatedUser)
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
