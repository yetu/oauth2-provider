package com.yetu.oauth2provider.routes

import com.yetu.oauth2provider.base.BaseRoutesSpec
import play.api.test.FakeRequest
import play.api.test.Helpers._

class HealthCheckSpec extends BaseRoutesSpec {

  "health controller" must {

    "return name and organization " in {
      val Some(result) = route(FakeRequest(GET, healthUrl))
      status(result) mustEqual (OK)
      contentAsString(result) must include("com.yetu")
      contentAsString(result) must include("oauth2provider")
      contentAsString(result) must include("alive")
    }
  }
}
