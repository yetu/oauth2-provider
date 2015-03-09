package com.yetu.oauth2provider.utils

import com.yetu.oauth2provider.base.BaseSpec

class StringUtilsSpec extends BaseSpec {

  "When isEmpty method receives a value " must {
    "return false when string contains no value" in {
      StringUtils.isFull(Some("")) mustBe false
    }

    "return false when string contains no value but empty spaces" in {
      StringUtils.isFull(Some("   ")) mustBe false
    }

    "return false when is None" in {
      StringUtils.isFull(None) mustBe false
    }

    "return true when there is a character" in {
      StringUtils.isFull(Some("a")) mustBe true
    }

    "return true when there are n character" in {
      StringUtils.isFull(Some(" actual value ")) mustBe true
    }
  }

  "When areAllEmptyItems method receives values " must {
    "return false when one of the strings contains no value" in {
      StringUtils.isAnyEmpty(Some("")) mustBe true
      StringUtils.isAnyEmpty(Some(""), Some(""), Some("")) mustBe true
      StringUtils.isAnyEmpty(Some(""), Some("value"), Some("second value")) mustBe true
    }

    "return true when all of the strings contains values" in {
      StringUtils.isAnyEmpty(Some("first value "), Some("second value"), Some("third value")) mustBe false
    }
  }

  "to toHashmark method" must {
    "generate valid hash url" in {
      val queryString = Map(
        "access_token" -> Seq("someString"),
        "expires_in" -> Seq("someNumber")
      )
      StringUtils.toHashmark("http://local.yetudev.com/", queryString) mustBe "http://local.yetudev.com/#access_token=someString&expires_in=someNumber"
      StringUtils.toHashmark("http://local.yetudev.com/#value=1", queryString) mustBe "http://local.yetudev.com/#value=1&access_token=someString&expires_in=someNumber"

    }
  }

}
