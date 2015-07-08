package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.base.BaseSpec
import com.yetu.oauth2provider.controllers.authentication.YetuPasswordValidator
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.utils.Config
import org.scalatestplus.play.OneAppPerSuite

class YetuPasswordValidatorSpec extends BaseSpec with OneAppPerSuite with TestRegistry {

  "Password validator" must {
    val validator = new YetuPasswordValidator()

    s"not accept passwords shorter than ${Config.minimumPasswordLength} characters" in {

      val shortPassword = String.format(s"%${Config.minimumPasswordLength - 1}s", "A")

      validator.validate(shortPassword).isLeft mustBe true

    }

    s"accept password longer than ${Config.minimumPasswordLength} characters" in {

      val validPassword = String.format(s"%${Config.minimumPasswordLength}s", "A!a1")

      validator.validate(validPassword).isRight mustBe true

    }

    s"not accept passwords without lowercase characters" in {

      val invalidPassword = String.format(s"%${Config.minimumPasswordLength}s", "A!1")

      validator.validate(invalidPassword).isLeft mustBe true

    }

    s"not accept passwords without uppercase characters" in {

      val invalidPassword = String.format(s"%${Config.minimumPasswordLength}s", "a!1")

      validator.validate(invalidPassword).isLeft mustBe true

    }

    s"not accept passwords without digits" in {

      val invalidPassword = String.format(s"%${Config.minimumPasswordLength}s", "a!A")

      validator.validate(invalidPassword).isLeft mustBe true

    }

  }

}
