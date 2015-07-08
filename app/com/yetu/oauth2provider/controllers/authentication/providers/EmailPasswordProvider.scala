package com.yetu.oauth2provider.controllers.authentication.providers

import play.api.Play._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import securesocial.controllers.ViewTemplates
import securesocial.core.AuthenticationResult.{ Authenticated, NavigationFlow }
import securesocial.core._
import securesocial.core.providers.utils.PasswordHasher
import securesocial.core.services.{ AvatarService, UserService }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmailPasswordProvider[U](userService: UserService[U],
    avatarService: Option[AvatarService],
    viewTemplates: ViewTemplates,
    passwordHashers: Map[String, PasswordHasher]) extends IdentityProvider with ApiSupport with Controller {

  override val id: String = EmailPasswordProvider.EmailPassword

  val InvalidCredentials = "securesocial.login.invalidCredentials"

  def authMethod: AuthenticationMethod = AuthenticationMethod.UserPassword

  def authenticateForApi(implicit request: Request[AnyContent]): Future[AuthenticationResult] = doAuthentication(apiMode = true)

  def authenticate()(implicit request: Request[AnyContent]): Future[AuthenticationResult] = doAuthentication()

  private def profileForCredentials(email: String, password: String): Future[Option[BasicProfile]] = {
    userService.findByEmailAndProvider(email, id).map { maybeUser =>
      for (
        user <- maybeUser;
        pinfo <- user.passwordInfo;
        hasher <- passwordHashers.get(pinfo.hasher) if hasher.matches(pinfo, password)
      ) yield {
        user
      }
    }
  }

  protected def authenticationFailedResult[A](apiMode: Boolean)(implicit request: Request[A]) = Future.successful {
    if (apiMode)
      AuthenticationResult.Failed("Invalid credentials")
    else
      NavigationFlow(badRequest(EmailPasswordProvider.loginForm, Some(InvalidCredentials)))
  }

  protected def withUpdatedAvatar(profile: BasicProfile): Future[BasicProfile] = {
    (avatarService, profile.email) match {
      case (Some(service), Some(e)) => service.urlFor(e).map {
        case url if url != profile.avatarUrl => profile.copy(avatarUrl = url)
        case _                               => profile
      }
      case _ => Future.successful(profile)
    }
  }

  private def doAuthentication[A](apiMode: Boolean = false)(implicit request: Request[A]): Future[AuthenticationResult] = {
    val form = EmailPasswordProvider.loginForm.bindFromRequest()
    form.fold(
      errors => Future.successful {
        if (apiMode)
          AuthenticationResult.Failed("Invalid credentials")
        else
          AuthenticationResult.NavigationFlow(badRequest(errors)(request))
      },
      credentials => {
        val (email, password) = credentials
        profileForCredentials(email, password).flatMap {
          case Some(profile) => withUpdatedAvatar(profile).map(Authenticated)
          case None          => authenticationFailedResult(apiMode)
        }
      })
  }

  private def badRequest[A](f: Form[(String, String)], msg: Option[String] = None)(implicit request: Request[A]): Result = {
    Results.BadRequest(viewTemplates.getLoginPage(f, msg))
  }
}

object EmailPasswordProvider {

  val EmailPassword = "userpass"

  private val SendWelcomeEmailKey = "securesocial.userpass.sendWelcomeEmail"
  private val Hasher = "securesocial.userpass.hasher"
  private val EnableTokenJob = "securesocial.userpass.enableTokenJob"
  private val SignupSkipLogin = "securesocial.userpass.signupSkipLogin"

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )

  lazy val sendWelcomeEmail = current.configuration.getBoolean(SendWelcomeEmailKey).getOrElse(true)
  lazy val hasher = current.configuration.getString(Hasher).getOrElse(PasswordHasher.id)
  lazy val enableTokenJob = current.configuration.getBoolean(EnableTokenJob).getOrElse(true)
  lazy val signupSkipLogin = current.configuration.getBoolean(SignupSkipLogin).getOrElse(false)
}