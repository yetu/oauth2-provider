package com.yetu.oauth2provider
package oauth2
package models

case class OAuth2Client(clientId: String,
  clientSecret: String,
  redirectURIs: List[String],
  grantTypes: Option[List[String]] = None,
  clientName: String,
  coreYetuClient: Boolean)
