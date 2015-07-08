package com.yetu.oauth2provider
package services
package data
package interface

import com.yetu.oauth2provider.oauth2.models.YetuUser

import scala.concurrent.Future

trait IPersonService extends ISecureSocialUserService {

  def deleteUser(id: String): Future[Unit]

  def updateUser(user: YetuUser): Future[Option[YetuUser]]

  def findUser(userId: String): Future[Option[YetuUser]]

  def addUser(user: YetuUser): Future[Option[YetuUser]]
}