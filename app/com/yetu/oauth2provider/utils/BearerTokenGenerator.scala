package com.yetu.oauth2provider
package utils

import java.security.SecureRandom

/*
 * Generates a Bearer Token with the length of 32 characters according to the
 * specification RFC6750 (http://http://tools.ietf.org/html/rfc6750)
 *
 * http://auconsil.blogspot.de/2013/06/create-bearer-token-in-scala.html
 */
object BearerTokenGenerator {

  val TOKEN_LENGTH = 32
  val TOKEN_CHARS =
    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._"
  val secureRandom = new SecureRandom()

  def generateToken: String =
    generateToken(TOKEN_LENGTH)

  def generateToken(tokenLength: Int): String =
    if (tokenLength == 0) ""
    else TOKEN_CHARS(secureRandom.nextInt(TOKEN_CHARS.length())) +
      generateToken(tokenLength - 1)
}

object UUIDGenerator {

  def uuid(): String = java.util.UUID.randomUUID.toString

}
