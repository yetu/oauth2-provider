package com.yetu.oauth2provider.services.data.interface

import com.yetu.oauth2provider.oauth2.models.{ IdentityId, YetuUser, ClientPermission }
import com.yetu.oauth2provider.models.DataUpdateRequest
import play.api.mvc.Result
import securesocial.core.{ PasswordInfo, BasicProfile }
import securesocial.core.services.SaveMode

import scala.concurrent.Future

trait ISecureSocialUserService {

  /**
   * Finds a SocialUser that maches the specified id
   *
   * @param providerId the provider id
   * @param userId the user id
   * @return an optional profile
   */
  def find(providerId: String, userId: String): Future[Option[BasicProfile]]

  /**
   * Finds a profile by email and provider
   *
   * @param email - the user email
   * @param providerId - the provider id
   * @return an optional profile
   */
  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]]

  /**
   * Saves a profile.  This method gets called when a user logs in, registers or changes his password.
   * This is your chance to save the user information in your backing store.
   *
   * @param profile the user profile
   * @param mode a mode that tells you why the save method was called
   */
  def save(profile: BasicProfile, mode: SaveMode): Future[YetuUser]

  /**
   * Links the current user to another profile
   *
   * @param current The current user instance
   * @param to the profile that needs to be linked to
   */
  def link(current: YetuUser, to: BasicProfile): Future[YetuUser]

  /**
   * Returns an optional PasswordInfo instance for a given user
   *
   * @param user a user instance
   * @return returns an optional PasswordInfo
   */
  def passwordInfoFor(user: YetuUser): Future[Option[PasswordInfo]]

  /**
   * Updates the PasswordInfo for a given user
   *
   * @param user a user instance
   * @param info the password info
   * @return
   */
  def updatePasswordInfo(user: YetuUser, info: PasswordInfo): Future[Option[BasicProfile]]

}