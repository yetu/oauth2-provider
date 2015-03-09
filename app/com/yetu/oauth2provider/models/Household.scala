package com.yetu.oauth2provider
package models

import play.api.libs.json._
/**
 * Contains the representation of household or householdId
 * as given to the infrastructure/gateway backend upon /household request
 */
object HouseholdModel {

  type HouseholdId = String

  case class Household(householdId: HouseholdId)

  /**
   * creates the default writes/reads object via scala macro, see
   * http://www.playframework.com/documentation/2.3.x/ScalaJsonInception
   * for details
   */
  implicit val householdFormat = Json.format[Household]

}
