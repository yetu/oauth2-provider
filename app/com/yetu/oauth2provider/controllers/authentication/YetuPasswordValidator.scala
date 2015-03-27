package com.yetu.oauth2provider.controllers.authentication

import securesocial.core.providers.utils.PasswordValidator

import com.yetu.oauth2provider.utils.Config

class YetuPasswordValidator extends PasswordValidator {

  override def validate(password: String): Either[(String, Seq[Any]), Unit] = {
    if (password.length >= Config.minimumPasswordLength) {
      Right(())
    } else
      Left((YetuPasswordValidator.InvalidPasswordMessage, Seq(Config.minimumPasswordLength)))
  }

}

object YetuPasswordValidator {
  val InvalidPasswordMessage = "securesocial.signup.invalidPassword"
}