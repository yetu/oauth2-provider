package com.yetu.oauth2provider
package models

import com.yetu.oauth2resource.model.ContactInfo
import play.api.libs.json.Json

/**
 * This class contains user basic information and contact information
 */
case class DataUpdateRequest(firstName: Option[String],
  lastName: Option[String],
  contactInfo: Option[ContactInfo])

object DataUpdateRequest {
  implicit val DataUpdateRequestFormat = Json.format[DataUpdateRequest]
}