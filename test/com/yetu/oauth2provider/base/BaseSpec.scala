package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.base.DefaultTestVariables
import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.utils.Config
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import play.api.test.FakeApplication

/**
 * Class to be extended for all Unit tests
 */
class BaseSpec extends WordSpec with MustMatchers
  with GeneratorDrivenPropertyChecks
  with TestRegistry
  with BeforeAndAfter
  with OptionValues
  with Inside
  with Inspectors
  with BaseMethods
  with DefaultTestVariables

