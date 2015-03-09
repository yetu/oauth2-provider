package com.yetu.oauth2provider
package oauth2
package models

case class AuthorizedClient(client: OAuth2Client, request: AuthorizeRequest, redirectUrl: String)
