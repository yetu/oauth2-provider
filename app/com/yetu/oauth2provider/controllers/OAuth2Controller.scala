package com.yetu.oauth2provider.controllers

import com.yetu.oauth2provider.oauth2.models.AuthorizedClient
import com.yetu.oauth2provider.registry.ServicesRegistry
import play.api.mvc.{ Result, Request, Controller }
import com.yetu.oauth2provider.utils.StringUtils

import scala.concurrent.Future
import scalaoauth2.provider.{ AuthorizationHandler, TokenEndpoint, OAuth2AsyncProvider }
import scala.concurrent.ExecutionContext.Implicits.global

trait OAuth2Controller extends Controller with OAuth2AsyncProvider

class OAuth2ImplicitControllerHelper(override val tokenEndpoint: TokenEndpoint) extends OAuth2Controller {

  def issueAccessTokenImplicitFlow[A, U](handler: AuthorizationHandler[U], authClient: AuthorizedClient)(implicit request: Request[A]): Future[Result] = {
    tokenEndpoint.handleRequest(request, handler).map {
      case Left(e) if e.statusCode == 400 => BadRequest(responseOAuthErrorJson(e)).withHeaders(responseOAuthErrorHeader(e))
      case Left(e) if e.statusCode == 401 => Unauthorized(responseOAuthErrorJson(e)).withHeaders(responseOAuthErrorHeader(e))
      case Right(r) => {
        val expiresIn = r.expiresIn map (v => "expires_in" -> Seq(v.toString))
        val queryString: Map[String, Seq[String]] = Map(
          "access_token" -> Seq(r.accessToken),
          "state" -> Seq(authClient.request.state)
        ) ++ expiresIn
        Redirect(StringUtils.toHashmark(authClient.redirectUrl, queryString)).withHeaders("Cache-Control" -> "no-store", "Pragma" -> "no-cache")
      }
    }
  }

}

