package com.yetu.oauth2provider.routes

import com.yetu.oauth2provider.base.{ AuthRoutesHelper, BaseRoutesSpec }
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.helper.CSRF

import scala.concurrent.Future

/**
 * Created by elisahilprecht on 16/03/15.
 */
class SetupSpec extends BaseRoutesSpec with AuthRoutesHelper {

  def requestReturnOk(urlRequest: String): Future[Result] = {
    val Some(result) = route(FakeRequest(GET, urlRequest))
    status(result) mustEqual (OK)
    return result;
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

  //  "registration controller" must {
  //
  //
  //
  //    s"POST on $signupUrl" in {
  //
  //
  //      val htmlSignupPage = requestReturnOk(signupUrl)
  //      val x = contentAsString(htmlSignupPage)
  //      val y = x.split("<input type=\"hidden\" name=\"csrfToken\" value=\"").toList
  //      val csrfToken = y.tail.head.split("\"/>").toList.head
  //      log(csrfToken)
  //
  //
  //      val parameters = Map(
  //        "csrfToken" -> Seq(csrfToken),
  //        "firstName" -> Seq("testFirstName"),
  //        "lastName" -> Seq("testLastName"),
  //        "email" -> Seq("email@email.email"),
  //        "password.password1" -> Seq("password"),
  //        "password.password2" -> Seq("password")
  //      )
  //      val result = postRequest(signupUrl, parameters)
  //      status(result) mustEqual SEE_OTHER
  //
  //    }
  //
  //  }
}
