package com.yetu.oauth2provider
package oauth2
package models

case class ClientScopes(clientId: String, scopes: Option[List[String]] = None)