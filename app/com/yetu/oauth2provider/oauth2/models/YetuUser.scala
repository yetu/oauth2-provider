package com.yetu.oauth2provider
package oauth2
package models

import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.DateUtility
import com.yetu.oauth2resource.model.ContactInfo
import org.joda.time.DateTime
import play.api.libs.json.Json
import securesocial.controllers.UserAgreement
import securesocial.core.{OAuth1Info, OAuth2Info, _}

/**
 * An implementation of user Profile.  Used to gather user information when users sign up and/or sign in.
 */
case class YetuUser(
    userId: String,
    providerId: String,
    firstName: String,
    lastName: String,
    fullName: String,
    email: String,
    avatarUrl: Option[String],
    authMethod: AuthenticationMethod,
    oAuth1Info: Option[OAuth1Info] = None,
    oAuth2Info: Option[OAuth2Info] = None,
    passwordInfo: Option[PasswordInfo] = None,
    registrationDate: Option[DateTime] = None,
    contactInfo: Option[ContactInfo] = None,
    publicKey: Option[YetuPublicKey] = None,
    userAgreement: Option[UserAgreement] = None) {

  def toBasicProfile = BasicProfile(
    providerId,
    userId,
    Some(firstName),
    Some(lastName),
    Some(fullName),
    Some(email),
    avatarUrl,
    authMethod,
    oAuth1Info,
    oAuth2Info,
    passwordInfo,
    userAgreement)
}

object YetuUser {
  implicit val formatOAuth1Info = Json.format[OAuth1Info]
  implicit val formatOAuth2Info = Json.format[OAuth2Info]
  implicit val formatPasswordInfo = Json.format[PasswordInfo]
  implicit val formatContactInfo = Json.format[ContactInfo]
  implicit val formatYetuPublicKey = Json.format[YetuPublicKey]
  implicit val formatUserAgreement = Json.format[UserAgreement]
  implicit val formatAuthenticationMethod = Json.format[AuthenticationMethod]
  implicit val formatYetuUser = Json.format[YetuUser]
}

object YetuUserHelper {
  def fromBasicProfile(profile: BasicProfile): YetuUser = {
    YetuUser(
      profile.userId,
      profile.providerId,
      profile.firstName.getOrElse(""),
      profile.lastName.getOrElse(""),
      profile.fullName.getOrElse(""),
      profile.email.getOrElse(""),
      profile.avatarUrl,
      profile.authMethod,
      profile.oAuth1Info,
      profile.oAuth2Info,
      profile.passwordInfo,
      Some(DateTime.now()),
      None,
      None,
      profile.userAgreement
    )
  }
  def fromJson(raw: String): YetuUser = {

    val user = Json.parse(raw)
    val firstName = (user \ "firstName").as[String]
    val lastName = (user \ "lastName").as[String]

    val publicKey = (user \ "key").asOpt[String] match {
      case Some(pk) => Some(new YetuPublicKey(pk))
      case _        => None
    }

    val agreement = new UserAgreement(
      acceptTermsAndConditions = true,
      DateUtility.utcStringToDateTime((user \ "agreementDate").as[String]))

    new YetuUser(
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
      registrationDate = Some(DateUtility.utcStringToDateTime((user \ "password").as[String])),
      contactInfo = None,
      publicKey,
      Some(agreement)
    )
  }

  def toJson(user: YetuUser) = {
    Json.obj(
      "id" -> user.userId,
      "provider" -> user.providerId,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "email" -> user.email,
      "registrationDate" -> user.registrationDate.map(registration => DateUtility.dateToUtcString(registration)),
      "agreementDate" -> user.userAgreement.map(agree => DateUtility.dateToUtcString(agree.acceptTermsAndConditionsDate)),
      "password" -> user.passwordInfo.map(_.password)
    )
  }
}