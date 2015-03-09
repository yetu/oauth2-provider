package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.views
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import play.twirl.api.{ Html, Txt }
import securesocial.controllers.MailTemplates
import securesocial.core.{ BasicProfile, RuntimeEnvironment }

class YetuMailTemplates(env: RuntimeEnvironment[_]) extends MailTemplates {

  def getSignUpEmail(token: String)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.signUpEmail(token)(request, lang)))
  }

  def getAlreadyRegisteredEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.alreadyRegisteredEmail(user)(request, lang, env)))
  }

  def getWelcomeEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.welcomeEmail(user)(request, lang, env)))
  }

  def getUnknownEmailNotice()(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.unknownEmailNotice()(request, lang)))
  }

  def getSendPasswordResetEmail(user: BasicProfile, token: String)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.passwordResetEmail(user, token)(request, lang, env)))
  }

  def getPasswordChangedNoticeEmail(user: BasicProfile)(implicit request: RequestHeader, lang: Lang): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.yetuAuthentication.mails.passwordChangedNotice(user)(request, lang, env)))
  }
}
