package com.yetu.oauth2provider.base

import com.yetu.oauth2resource.utils.RoutesHelper
import org.scalatestplus.play._
import play.api.test.FakeApplication

/**
 * using new integration of ScalaTest with Play (instead of Specs2 with Play)
 * see these links:
 * http://www.playframework.com/documentation/2.3.x/ScalaTestingWithScalaTest
 * http://www.playframework.com/documentation/2.3.x/ScalaFunctionalTestingWithScalaTest
 */
class BaseRoutesSpec extends PlaySpec with OneAppPerSuite with BaseMethods with RoutesHelper with AuthRoutesHelper {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      withGlobal = Some(TestGlobal))

}

