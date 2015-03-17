package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.views
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import play.twirl.api.{ Html, Txt }
import securesocial.controllers.MailTemplates
import securesocial.core.{ BasicProfile, RuntimeEnvironment }

class YetuMailTemplates(env: RuntimeEnvironment[_]) extends MailTemplates {
  implicit val implicitEnv = env
  def getSignUpEmail(token: String)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.signUpEmail(token)))
  }

  def getAlreadyRegisteredEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.alreadyRegisteredEmail(user)))
  }

  def getWelcomeEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.welcomeEmail(user)))
  }

  def getUnknownEmailNotice()(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.unknownEmailNotice()))
  }

  def getSendPasswordResetEmail(user: BasicProfile, token: String)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.passwordResetEmail(user, token)))
  }

  def getPasswordChangedNoticeEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.passwordChangedNotice(user)))
  }
}
