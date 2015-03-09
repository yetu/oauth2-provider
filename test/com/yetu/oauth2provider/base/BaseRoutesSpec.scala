package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.testdata.DefaultTestVariables
import com.yetu.oauth2provider.utils.Config._
import com.yetu.oauth2resource.utils.RoutesHelper
import org.scalatestplus.play._
import play.api.libs.json.{ JsNull, JsValue }
import play.api.mvc.{ Headers, AnyContentAsEmpty, Result }
import play.api.test.Helpers._
import play.api.test.{ FakeApplication, FakeHeaders, FakeRequest }

import scala.concurrent.Future

/**
 * using new integration of ScalaTest with Play (instead of Specs2 with Play)
 * see these links:
 * http://www.playframework.com/documentation/2.3.x/ScalaTestingWithScalaTest
 * http://www.playframework.com/documentation/2.3.x/ScalaFunctionalTestingWithScalaTest
 */
class BaseRoutesSpec extends PlaySpec with OneAppPerSuite with BaseMethods with RoutesHelper with AuthRoutesHelper {

  import com.yetu.oauth2provider.oauth2.models.OAuth2Client
  import com.yetu.oauth2provider.utils.Config._

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal))

}

