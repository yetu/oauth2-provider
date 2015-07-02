package com.yetu.oauth2provider.services.data.api

import com.yetu.oauth2provider.oauth2.models.{ YetuUser, YetuUserHelper }
import com.yetu.oauth2provider.services.data.interface.{ IMailTokenService, IPersonService }
import com.yetu.oauth2provider.utils.NamedLogger
import play.api.Play.current
import play.api.libs.ws._
import play.mvc.Http
import securesocial.core.providers.MailToken
import securesocial.core.services.{ SaveMode, UserService }
import securesocial.core.{ BasicProfile, PasswordInfo }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class APIPersonService(mailTokenService: IMailTokenService) extends IPersonService with APIHelper with NamedLogger with UserService[YetuUser] {

  override def saveToken(token: MailToken): Future[MailToken] = {
    mailTokenService.saveToken(token)
  }

  override def deleteToken(uuid: String): Future[Option[MailToken]] = {
    mailTokenService.deleteToken(uuid)
  }

  override def findToken(token: String): Future[Option[MailToken]] = {
    mailTokenService.findToken(token)
  }

  override def deleteExpiredTokens(): Unit = {
    mailTokenService.deleteExpiredTokens()
  }

  override def updateUser(user: YetuUser) = {
    WS.url(urlForResource("users", user.userId, Version1)).put(YetuUserHelper.toJson(user)).map(response => {
      Some(user)
    })
  }

  override def deleteUser(id: String) = {
    WS.url(urlForResource("users", id, Version1)).delete().map(_ => Unit)
  }

  override def findUser(userId: String) = {
    WS.url(urlForResource("users", userId, Version1)).get().map(response => {
      if (response.status == Http.Status.OK) {

        Some(YetuUser.fromJson(response.body))

      } else None
    })
  }

  override def addUser(user: YetuUser) = {
    WS.url(url("users", Version1)).post(YetuUserHelper.toJson(user)).map(response => {

      findUser(user.userId).map(u => {
        u
      })

      Some(user)
    })
  }

  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    WS.url(urlForResource("users/email", email, Version1)).get().map(response => {
      if (response.status == Http.Status.OK) {

        val user = YetuUser.fromJson(response.body)
        if (user.providerId.equals(providerId)) {

          Some(user)

        } else None
      } else None
    })
  }

  override def save(profile: BasicProfile, mode: SaveMode) = {
    val result = mode match {
      case SaveMode.LoggedIn       => findUser(profile.userId)
      case SaveMode.PasswordChange => updateUser(profile.asInstanceOf[YetuUser])
      case SaveMode.SignUp         => addUser(profile.asInstanceOf[YetuUser])
    }

    result.map(_.orNull)
  }

  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    findUser(userId).map {

      case Some(user) =>
        if (user.providerId.eq(providerId)) {
          Some(user)
        } else None

      case _ => None
    }
  }

  override def link(current: YetuUser, to: BasicProfile): Future[YetuUser] = ???

  override def passwordInfoFor(user: YetuUser): Future[Option[PasswordInfo]] = {
    findUser(user.userId).map {
      case Some(u) => u.passwordInfo
      case _       => None
    }
  }

  override def updatePasswordInfo(user: YetuUser, info: PasswordInfo): Future[Option[BasicProfile]] = {
    updateUser(user.copyUser(passwordInfo = Some(info)))
  }
}
