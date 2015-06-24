package com.yetu.oauth2provider
package services
package data
package interface

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.YetuUser
import play.api.mvc.Result

trait IPersonService extends ISecureSocialUserService {

  def updateUserProfile(yetuUser: YetuUser, request: DataUpdateRequest): Result

  def deleteUser(email: String)

  def findYetuUser(userId: String): Option[YetuUser]

  def addNewUser(user: YetuUser): YetuUser
}