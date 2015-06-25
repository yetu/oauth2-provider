package com.yetu.oauth2provider.services.data.api

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.IPersonService
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.DateUtility
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws._
import play.mvc.Http
import securesocial.controllers.UserAgreement
import securesocial.core.services.SaveMode
import securesocial.core.{ AuthenticationMethod, BasicProfile, PasswordInfo }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class APIPersonService extends IPersonService with APIHelper {

  override def updateUserProfile(yetuUser: YetuUser, request: DataUpdateRequest) = {
    ???
  }

  override def deleteUser(id: String) = {
    WS.url(urlForResource(Version1, "users", id)).delete().map(_ => Unit)
  }

  override def findYetuUser(userId: String) = {
    WS.url(urlForResource(Version1, "users", userId)).get().map(response => {
      if (response.status == Http.Status.OK) {

        val user = Json.parse(response.body)
        val firstName = (user \ "id").as[String]
        val lastName = (user \ "id").as[String]

        val publicKey = (user \ "publicKey").asOpt[String] match {
          case Some(pk) => Some(new YetuPublicKey(pk))
          case _        => None
        }

        val agreement = new UserAgreement(
          acceptTermsAndConditions = true,
          DateUtility.utcStringToDateTime((user \ "agreementDate").as[String]))

        Some(new YetuUser(
          (user \ "id").as[String],
          (user \ "provider").as[String],
          firstName,
          lastName,
          firstName + " " + lastName,
          (user \ "email").as[String],
          avatarUrl = None,
          authMethod = AuthenticationMethod.OAuth2,
          oAuth1Info = None,
          oAuth2Info = None,
          passwordInfo = Some(PasswordInfo("bcrypt", (user \ "password").as[String], None)),
          registrationDate = Some(DateUtility.utcStringToDate((user \ "password").as[String])),
          contactInfo = None,
          publicKey,
          Some(agreement)
        ))

      } else None
    })
  }

  override def addNewUser(user: YetuUser) = ???

  override def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = ???

  override def save(profile: BasicProfile, mode: SaveMode) = ???

  override def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    findYetuUser(userId).map {

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
