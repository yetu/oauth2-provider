package com.yetu.oauth2provider
package oauth2
package models

import _root_.java.util.Date

import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2resource.model.ContactInfo
import play.api.libs.json.Json
import securesocial.controllers.UserAgreement
import securesocial.core.{ OAuth1Info, OAuth2Info, _ }

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
    registrationDate: Option[Date] = None,
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
      None,
      None,
      None,
      profile.userAgreement
    )
  }
}