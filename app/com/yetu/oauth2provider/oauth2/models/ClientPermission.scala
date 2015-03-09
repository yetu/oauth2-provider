package com.yetu.oauth2provider
package oauth2
package models

case class ClientPermission(clientId: String, scopes: Option[List[String]] = None)