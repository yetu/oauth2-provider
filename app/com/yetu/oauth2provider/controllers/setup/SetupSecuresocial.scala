package com.yetu.oauth2provider.controllers.setup

import com.yetu.oauth2provider.oauth2.models.YetuUser
import play.api.data.Forms._

import play.api.data._
import play.api.i18n.Messages
import play.filters.csrf._
import play.api.mvc.Action
import securesocial.core._
import securesocial.core.authenticator.CookieAuthenticator
import securesocial.core.providers.UsernamePasswordProvider
import securesocial.core.providers.utils._
import securesocial.core.services.SaveMode
import securesocial.controllers._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.{ Await, Future }

/**
 * A yetu Registration controller that uses the YetuUserProfile as the user type
 *
 * @param env the environment
 */
class Registration(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseRegistration[YetuUser] {

}