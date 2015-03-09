package com.yetu.oauth2provider.utils

import com.yetu.oauth2provider.base.BaseSpec

class DateUtilitySpec extends BaseSpec {

  "DateUtility" must {

    "correctly create now and expired time in UTC" in {
      val actual = DateUtility.unixSecondsDefaultExpiration() - DateUtility.unixSecondsNow()
      val expected = Config.OAuth2.accessTokenExpirationInSeconds
      actual mustEqual (expected)

      println(DateUtility.unixSecondsNow())
      println("if in doubt, go compare the line above with the value you get at http://www.unixtimestamp.com/index.php")

    }

  }

}
