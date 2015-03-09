package com.yetu.oauth2provider
package oauth2
package errors

import scalaoauth2.provider.OAuthError

class InvalidState(description: String = "") extends OAuthError(description) {
  override val errorType = "invalid_state"
}