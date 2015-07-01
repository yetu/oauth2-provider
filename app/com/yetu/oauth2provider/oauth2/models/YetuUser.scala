package com.yetu.oauth2provider
package oauth2
package models

import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.DateUtility
import com.yetu.oauth2resource.model.ContactInfo
import org.joda.time.DateTime
import play.api.libs.json.Json
import securesocial.controllers.UserAgreement
import securesocial.core.{ OAuth1Info, OAuth2Info, _ }

/**
 * An implementation of user Profile.  Used to gather user information when users sign up and/or sign in.
 */
class YetuUser(
  override val userId: String,
  override val providerId: String,
  override val firstName: Option[String],
  override val lastName: Option[String],
  override val fullName: Option[String],
  override val email: Option[String],
  override val avatarUrl: Option[String],
  override val authMethod: AuthenticationMethod,
  override val oAuth1Info: Option[OAuth1Info] = None,
  override val oAuth2Info: Option[OAuth2Info] = None,
  override val passwordInfo: Option[PasswordInfo] = None,
  override val userAgreement: Option[UserAgreement] = None,
  var registrationDate: Option[DateTime] = None,
  var contactInfo: Option[ContactInfo] = None,
  val publicKey: Option[YetuPublicKey] = None)

    extends BasicProfile(
      providerId: String,
      userId: String,
      firstName: Option[String],
      lastName: Option[String],
      fullName: Option[String],
      email: Option[String],
      avatarUrl: Option[String],
      authMethod: AuthenticationMethod,
      oAuth1Info: Option[OAuth1Info],
      oAuth2Info: Option[OAuth2Info],
      passwordInfo: Option[PasswordInfo],
      userAgreement: Option[UserAgreement]) {

  def copyUser(userId: String = userId,
    providerId: String = providerId,
    firstName: Option[String] = firstName,
    lastName: Option[String] = lastName,
    fullName: Option[String] = fullName,
    email: Option[String] = email,
    avatarUrl: Option[String] = avatarUrl,
    authMethod: AuthenticationMethod = authMethod,
    oAuth1Info: Option[OAuth1Info] = oAuth1Info,
    oAuth2Info: Option[OAuth2Info] = oAuth2Info,
    passwordInfo: Option[PasswordInfo] = passwordInfo,
    userAgreement: Option[UserAgreement] = userAgreement,
    registrationDate: Option[DateTime] = registrationDate,
    contactInfo: Option[ContactInfo] = contactInfo,
    publicKey: Option[YetuPublicKey] = publicKey) = {

    new YetuUser(
      userId,
      providerId,
      firstName,
      lastName,
      fullName,
      email,
      avatarUrl,
      authMethod,
      oAuth1Info,
      oAuth2Info,
      passwordInfo,
      userAgreement,
      registrationDate,
      contactInfo,
      publicKey)
  }
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

  def apply(userId: String,
    providerId: String,
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarUrl: Option[String],
    authMethod: AuthenticationMethod,
    oAuth1Info: Option[OAuth1Info] = None,
    oAuth2Info: Option[OAuth2Info] = None,
    passwordInfo: Option[PasswordInfo] = None,
    userAgreement: Option[UserAgreement] = None,
    registrationDate: Option[DateTime] = None,
    contactInfo: Option[ContactInfo] = None,
    publicKey: Option[YetuPublicKey] = None) = {

    new YetuUser(userId,
      providerId,
      firstName,
      lastName,
      fullName,
      email,
      avatarUrl,
      authMethod,
      oAuth1Info,
      oAuth2Info,
      passwordInfo,
      userAgreement,
      registrationDate,
      contactInfo,
      publicKey)
  }

  def unapply(user: YetuUser): Option[(String, String, Option[String], Option[String], Option[String], Option[String], Option[String], AuthenticationMethod, Option[OAuth1Info], Option[OAuth2Info], Option[PasswordInfo], Option[UserAgreement], Option[DateTime], Option[ContactInfo], Option[YetuPublicKey])] =
    Some((user.userId,
      user.providerId,
      user.firstName,
      user.lastName,
      user.fullName,
      user.email,
      user.avatarUrl,
      user.authMethod,
      user.oAuth1Info,
      user.oAuth2Info,
      user.passwordInfo,
      user.userAgreement,
      user.registrationDate,
      user.contactInfo,
      user.publicKey))

  def fromJson(raw: String): YetuUser = {

    val user = Json.parse(raw)
    val firstName = (user \ "firstName").asOpt[String]
    val lastName = (user \ "lastName").asOpt[String]
    val fullName = Some(firstName.getOrElse("") + " " + lastName.getOrElse(""))

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
      fullName,
      (user \ "email").asOpt[String],
      avatarUrl = None,
      authMethod = AuthenticationMethod.OAuth2,
      oAuth1Info = None,
      oAuth2Info = None,
      passwordInfo = Some(PasswordInfo("bcrypt", (user \ "password").as[String], None)),
      Some(agreement),
      registrationDate = Some(DateUtility.utcStringToDateTime((user \ "registrationDate").as[String])),
      contactInfo = None,
      publicKey
    )
  }
}

object YetuUserHelper {

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