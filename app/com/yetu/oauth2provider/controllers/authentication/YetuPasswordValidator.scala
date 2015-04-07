package com.yetu.oauth2provider.controllers.authentication

import securesocial.core.providers.utils.PasswordValidator

import com.yetu.oauth2provider.utils.Config

import scala.util.matching.Regex

class YetuPasswordValidator extends PasswordValidator {
  class RichRegex(underlying: Regex) {
    def matches(s: String) = underlying.pattern.matcher(s).matches
  }
  implicit def regexToRichRegex(r: Regex) = new RichRegex(r)

  def hasValidLength(implicit password: String): Boolean = {
    password.length >= Config.minimumPasswordLength
  }
  def containsUppercase(implicit password: String): Boolean = {
    """.*[A-Z]+.*""".r matches password
  }
  def containsLowercase(implicit password: String): Boolean = {
    """.*[a-z]+.*""".r matches password
  }
  def containsDigit(implicit password: String): Boolean = {
    """.*[0-9]+.*""".r matches password
  }

  override def validate(password: String): Either[(String, Seq[Any]), Unit] = {
    implicit val implicitPassword = password

    if (hasValidLength && containsDigit && containsUppercase && containsLowercase) {
      Right(())
    } else {
      Left((YetuPasswordValidator.InvalidPasswordMessage, Seq(Config.minimumPasswordLength)))
    }
  }

}

object YetuPasswordValidator {
  val InvalidPasswordMessage = "securesocial.signup.invalidPassword"
}
