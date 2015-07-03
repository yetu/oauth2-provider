package com.yetu.oauth2provider
package models

import play.api.data.Form
import play.api.data.Forms._

case class Permissions(scopes: String, client_id: String, redirect_uri: String, state: String)

object Permission {

  val permissionsForm = Form[Permissions](
    mapping(
      "scopes" -> text,
      "client_id" -> text,
      "redirect_uri" -> text,
      "state" -> text

    )(Permissions.apply)(Permissions.unapply)
  )

}
