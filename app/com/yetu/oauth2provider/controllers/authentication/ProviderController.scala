package com.yetu.oauth2provider.controllers.authentication

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.IPersonService
import com.yetu.oauth2provider.utils.Config.SessionStatusCookie
import play.api.i18n.Messages
import play.api.mvc.{ Action, AnyContent, Cookie, Session }
import securesocial.controllers.BaseProviderController
import securesocial.core._
import securesocial.core.authenticator.CookieAuthenticator
import securesocial.core.services.SaveMode

import scala.concurrent.Future

/**
 * This class copies code from securesocial, as there is no other way to include setting a second cookie upon login.
 * All extra code that is a modification of original securesocial code is tagged with |-EXTRA-|
 *
 */
class ProviderController(personService: IPersonService)(implicit override val env: RuntimeEnvironment[YetuUser]) extends BaseProviderController[YetuUser] {

  import securesocial.controllers.ProviderControllerHelper._
  import securesocial.core.utils.SimpleResultMethods

  val logger = play.api.Logger("com.yetu.oauth2provider.ProviderController") // |-EXTRA-|

  /**
   * |-EXTRA-|
   */
  def getAdditionalSessionStateCookie(userId: String): Cookie = {
    val fullUser: Option[YetuUser] = personService.findYetuUser(userId)
    val userUUID = fullUser.map(_.uid).getOrElse("unknownUser")

    Cookie(
      SessionStatusCookie.cookieName,
      userUUID,
      if (CookieAuthenticator.makeTransient)
        CookieAuthenticator.Transient
      else Some(CookieAuthenticator.absoluteTimeoutInSeconds),
      SessionStatusCookie.cookiePath,
      SessionStatusCookie.cookieDomain,
      secure = SessionStatusCookie.cookieSecure,
      httpOnly = SessionStatusCookie.cookieHttpOnly
    )
  }

  override def authenticate(provider: String, redirectTo: Option[String] = None) = handleAuth(provider, redirectTo)

  override def authenticateByPost(provider: String, redirectTo: Option[String] = None) = handleAuth(provider, redirectTo)

  /**
   * Common method to handle GET and POST authentication requests
   *
   * @param provider the provider that needs to handle the flow
   * @param redirectTo the url the user needs to be redirected to after being authenticated
   */
  private def handleAuth(provider: String, redirectTo: Option[String]): Action[AnyContent] = UserAwareAction.async { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global
    val authenticationFlow = request.user.isEmpty
    val modifiedSession = overrideOriginalUrl(request.session, redirectTo)

    env.providers.get(provider).map {
      _.authenticate().flatMap {
        case denied: AuthenticationResult.AccessDenied =>
          Future.successful(Redirect(env.routes.loginPageUrl).flashing("error" -> Messages("securesocial.login.accessDenied")))
        case failed: AuthenticationResult.Failed =>
          logger.error(s"[securesocial] authentication failed, reason: ${failed.error}")
          throw new AuthenticationException()
        case flow: AuthenticationResult.NavigationFlow => Future.successful {
          redirectTo.map { url =>
            flow.result.addToSession(SecureSocial.OriginalUrlKey -> url)
          } getOrElse flow.result
        }
        case authenticated: AuthenticationResult.Authenticated =>
          if (authenticationFlow) {
            val profile = authenticated.profile
            env.userService.find(profile.providerId, profile.userId).flatMap { maybeExisting =>
              val mode = if (maybeExisting.isDefined) SaveMode.LoggedIn else SaveMode.SignUp
              env.userService.save(authenticated.profile, mode).flatMap { userForAction =>
                logger.debug(s"[securesocial] user completed authentication: provider = ${profile.providerId}, userId: ${profile.userId}, mode = $mode")
                val evt = if (mode == SaveMode.LoggedIn) new LoginEvent(userForAction) else new SignUpEvent(userForAction)
                val sessionAfterEvents = Events.fire(evt).getOrElse(request.session)
                import scala.concurrent.ExecutionContext.Implicits.global
                builder().fromUser(userForAction).flatMap { authenticator =>
                  Redirect(toUrl(sessionAfterEvents))
                    .withCookies(getAdditionalSessionStateCookie(profile.userId)) // |-EXTRA-|
                    .withSession(sessionAfterEvents -
                      SecureSocial.OriginalUrlKey -
                      IdentityProvider.SessionId -
                      OAuth1Provider.CacheKey).startingAuthenticator(authenticator)
                }
              }
            }
          } else {
            request.user match {
              case Some(currentUser) =>
                for (
                  linked <- env.userService.link(currentUser, authenticated.profile);
                  updatedAuthenticator <- request.authenticator.get.updateUser(linked);
                  result <- Redirect(toUrl(modifiedSession)).withSession(modifiedSession -
                    SecureSocial.OriginalUrlKey -
                    IdentityProvider.SessionId -
                    OAuth1Provider.CacheKey).touchingAuthenticator(updatedAuthenticator)
                ) yield {
                  logger.debug(s"[securesocial] linked $currentUser to: providerId = ${authenticated.profile.providerId}")
                  result
                }
              case _ =>
                Future.successful(Unauthorized)
            }
          }
      } recover {
        case e =>
          logger.error("Unable to log user in. An exception was thrown", e)
          Redirect(env.routes.loginPageUrl).flashing("error" -> Messages("securesocial.login.errorLoggingIn"))
      }
    } getOrElse {
      Future.successful(NotFound)
    }
  }

  private def overrideOriginalUrl(session: Session, redirectTo: Option[String]) = redirectTo match {
    case Some(url) =>
      session + (SecureSocial.OriginalUrlKey -> url)
    case _ =>
      session
  }

  private def builder() = {
    env.authenticatorService.find(CookieAuthenticator.Id).getOrElse {
      logger.error(s"[securesocial] missing CookieAuthenticatorBuilder")
      throw new AuthenticationException()
    }
  }
}
