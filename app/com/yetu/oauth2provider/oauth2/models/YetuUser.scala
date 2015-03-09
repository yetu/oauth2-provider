package com.yetu.oauth2provider
package oauth2
package models

import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2resource.model.ContactInfo
import securesocial.core._
import _root_.java.util.Date

import securesocial.core.{ OAuth2Info, OAuth1Info }

//TODO: deprecated, get rid of this.
case class IdentityId(userId: String, providerId: String)

/**
 * An implementation of user Profile.  Used to gather user information when users sign up and/or sign in.
 */
case class YetuUser(identityId: IdentityId, uid: String, firstName: String, lastName: String, fullName: String, email: Option[String],
    avatarUrl: Option[String], authMethod: AuthenticationMethod,
    oAuth1Info: Option[OAuth1Info] = None,
    oAuth2Info: Option[OAuth2Info] = None,
    passwordInfo: Option[PasswordInfo] = None,
    registrationDate: Option[Date] = None,
    contactInfo: Option[ContactInfo] = None,
    imageUrl: Option[String] = None,
    publicKey: Option[YetuPublicKey] = None) {

  //TODO: clean this up.
  val userId = identityId.userId
  val providerId = identityId.providerId
  val main: BasicProfile = BasicProfile(providerId, userId, Some(firstName), Some(lastName), Some(fullName), email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo)
  val identities: List[BasicProfile] = List(main)

  def toBasicProfile = BasicProfile(providerId, userId, Some(firstName), Some(lastName), Some(fullName), email, avatarUrl, authMethod, oAuth1Info, oAuth2Info, passwordInfo)

}

object YetuUserHelper {
  def fromBasicProfile(profile: BasicProfile, uuid: String): YetuUser = {
    YetuUser(IdentityId(profile.userId, profile.providerId),
      uuid,
      profile.firstName.getOrElse(""), //TODO: ?
      profile.lastName.getOrElse(""),
      profile.fullName.getOrElse(""),
      profile.email,
      profile.avatarUrl,
      profile.authMethod,
      profile.oAuth1Info,
      profile.oAuth2Info,
      profile.passwordInfo,
      None,
      None,
      None
    )
  }
}

object Temp {
  import scalaoauth2.provider.AuthInfo
  type AuthInformation = AuthInfo[YetuUser]
}

/*
case class BasicProfile(
  providerId: String,
  userId: String,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarUrl: Option[String],
  authMethod: AuthenticationMethod,
  oAuth1Info: Option[OAuth1Info] = None,
  oAuth2Info: Option[OAuth2Info] = None,
  passwordInfo: Option[PasswordInfo] = None
 */ 