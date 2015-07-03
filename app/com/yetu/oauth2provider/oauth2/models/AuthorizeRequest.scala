package com.yetu.oauth2provider
package oauth2
package models

import scalaoauth2.provider.RequestBase
import com.yetu.oauth2provider.oauth2.OAuth2Protocol.AuthorizeParameters

case class AuthorizeRequest(headers: Map[String, Seq[String]], params: Map[String, Seq[String]]) extends RequestBase(headers, params) {
  def clientId: String = requireParam(AuthorizeParameters.CLIENT_ID)

  def responseType: String = requireParam(AuthorizeParameters.RESPONSE_TYPE)

  def state: String = requireParam(AuthorizeParameters.STATE)

  def redirectUri: String = requireParam(AuthorizeParameters.REDIRECT_URI)

  def scopes: Option[String] = param(AuthorizeParameters.SCOPE)
}