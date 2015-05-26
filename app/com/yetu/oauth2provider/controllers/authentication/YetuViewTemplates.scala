package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.views
import play.api.data.Form
import play.api.i18n.{ Lang, Messages }
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import securesocial.controllers.{ ChangeInfo, RegistrationInfo, ViewTemplates }
import securesocial.core.RuntimeEnvironment

/**
 * This controller class is a custom version of secure social's default, pointing to the templates
 * under /views/yetuAuthentication/
 */
class YetuViewTemplates(env: RuntimeEnvironment[_]) extends ViewTemplates {
  implicit val implicitEnv = env

  override def getLoginPage(form: Form[(String, String)], msg: Option[String])(implicit request: RequestHeader, lang: Lang): Html = {

    val requestReferer = request.headers.get("Referer").getOrElse("");
    val requestMsg = request.flash.get("success").getOrElse("");
    if (requestReferer.contains(Messages("check.signup")(lang)) && requestMsg.contains(Messages("check.thankyou")(lang))) {
      views.html.yetuAuthentication.login(form, msg, true, false) (request, lang, env)
    } else if (requestReferer.contains(Messages("check.reset")(lang)) && requestMsg.contains(Messages("check.thankyou")(lang))) {
      views.html.yetuAuthentication.login(form, msg, false, true)(request, lang, env)
    } else {
      views.html.yetuAuthentication.login(form, msg, false, false) (request, lang, env)
    }
  }

  override def getSignUpPage()(implicit request: RequestHeader, lang: Lang): Html = {
    Html("")
  }

  override def getConfirmedSignUpPage()(implicit request: RequestHeader, lang: Lang): Html = {
    views.html.setup.confirmedmail()
  }

  override def getStartSignUpPage(form: Form[RegistrationInfo])(implicit request: RequestHeader, lang: Lang): Html = {
    views.html.setup.startSignUpForSetup(form)
  }

  override def getStartResetPasswordPage(form: Form[String])(implicit request: RequestHeader, lang: Lang): Html = {
    views.html.yetuAuthentication.Registration.startResetPassword(form)(request, lang, env)
  }

  override def getResetPasswordPage(form: Form[(String, String)], token: String)(implicit request: RequestHeader, lang: Lang): Html = {
    views.html.yetuAuthentication.Registration.resetPasswordPage(form, token)(request, lang, env)
  }

  override def getPasswordChangePage(form: Form[ChangeInfo])(implicit request: RequestHeader, lang: Lang): Html = {
    views.html.yetuAuthentication.passwordChange(form)(request, lang, env)
  }

  override def getNotAuthorizedPage(implicit request: RequestHeader, lang: Lang): Html = {
    views.html.yetuAuthentication.notAuthorized()(request, lang, env)
  }

}
