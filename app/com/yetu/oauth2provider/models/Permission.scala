package com.yetu.oauth2provider
package models

import play.api.data.Form
import play.api.data.Forms._

case class Permissions(client_id: String, redirect_uri: String, state: String)

object Permission {

  val permissionsForm = Form[Permissions](
    mapping(
      "client_id" -> text,
      "redirect_uri" -> text,
      "state" -> text

    )(Permissions.apply)(Permissions.unapply)
  )

}
