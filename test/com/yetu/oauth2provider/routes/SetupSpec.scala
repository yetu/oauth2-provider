package com.yetu.oauth2provider.routes

import com.yetu.oauth2provider.base.BaseRoutesSpec
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class SetupSpec extends BaseRoutesSpec {

  def requestReturnOk(urlRequest: String): Future[Result] = {
    val Some(result) = route(FakeRequest(GET, urlRequest))
    status(result) mustEqual (OK)
    return result
  }

  "setup controller" must {
    "return registration page with title 'Registration'" in {
      val result = requestReturnOk(setupRegistrationUrl)
      contentAsString(result) must include("<title>Registration</title>")
    }
    "return confirm mail page with title 'Confirm e-mail'" in {
      val result = requestReturnOk(setupConfirmMailUrl)
      contentAsString(result) must include("<title>Confirm e-mail</title>")
    }
    "return confirmed mail page with title confirm 'Successfully confirmed mail'" in {
      val result = requestReturnOk(setupConfirmedMailUrl)
      contentAsString(result) must include("<title>Successfully confirmed mail</title>")
    }
    "return download page with title confirm 'Download'" in {
      val result = requestReturnOk(setupDownloadUrl)
      contentAsString(result) must include("<title>Download</title>")
    }
  }
}
