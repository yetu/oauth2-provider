package com.yetu.oauth2provider
package services
package data
package iface

import com.yetu.oauth2provider.oauth2.models.{ IdentityId, YetuUser, ClientPermission }
import com.yetu.oauth2provider.models.DataUpdateRequest
import play.api.mvc.Result
import securesocial.core.{ PasswordInfo, BasicProfile }
import securesocial.core.services.SaveMode

import scala.concurrent.Future

trait IPersonService extends ISecureSocialUserService {

  def updateUserProfile(yetuUser: YetuUser, request: DataUpdateRequest): Result

  def deleteUser(email: String)

  def findYetuUser(userId: String): Option[YetuUser]

  def addNewUser(user: YetuUser): YetuUser
}