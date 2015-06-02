package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.registry.{IntegrationTestRegistry, TestRegistry}
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

/**
 * Class to be extended for all Unit tests
 */
class BaseIntegrationSpec extends WordSpec with MustMatchers
with GeneratorDrivenPropertyChecks
with IntegrationTestRegistry
with BeforeAndAfter
with OptionValues
with Inside
with Inspectors
with BaseMethods
with DefaultTestVariables

