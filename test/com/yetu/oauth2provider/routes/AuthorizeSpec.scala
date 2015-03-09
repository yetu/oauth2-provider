package com.yetu.oauth2provider
package routes

import com.yetu.oauth2provider.base.BaseRoutesSpec
import play.api.test.Helpers._

class AuthorizeSpec extends BaseRoutesSpec {

  val authorize = "/oauth2/authorize"

  //("breaks on NoSuchElementException: None.get (Application.scala:62) -> redirectWithAuthCode(client_id.get")
  s"GET $authorize endpoint  " must {
    "not break when no parameters" in {
      val response = getRequest(authorize)
      status(response) mustEqual (401)

    }
  }

}

