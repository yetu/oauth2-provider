package com.yetu.oauth2provider
package services
package data
package interface

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.YetuUser

import scala.concurrent.Future

trait IPersonService extends ISecureSocialUserService {

  def updateUserProfile(yetuUser: YetuUser, data: DataUpdateRequest): Future[Unit]

  def deleteUser(id: String): Future[Unit]

  def findYetuUser(userId: String): Future[Option[YetuUser]]

  def addNewUser(user: YetuUser): Future[Option[YetuUser]]
}