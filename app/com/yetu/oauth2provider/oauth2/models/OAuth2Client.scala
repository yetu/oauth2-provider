package com.yetu.oauth2provider
package oauth2
package models

case class OAuth2Client(clientId: String,
  clientSecret: String,
  redirectURIs: List[String], //TODO: should be NonEmptyList from ScalaZ
  grantTypes: Option[List[String]] = None,
  scopes: Option[List[String]] = None,
  clientName: String,
  coreYetuClient: Boolean)
