package com.yetu.oauth2provider.signature

import java.util
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import net.adamcin.httpsig.api._
import play.api.Logger
import play.api.mvc.Headers

object SignatureHelper {

  lazy val logger = Logger("com.yetu.oauth2provider.utils.SignatureHelper")

  /**
   * Converts playframework request headers to a java `RequestContent` that can be used for verification.
   */
  def convertHeaders(headers: Headers, headersToSign: List[String]): RequestContent = {
    val requestContentBuilder = new RequestContent.Builder()

    logger.debug(s"headersToSign = $headersToSign, headers= $headers")

    for (headerKey <- headersToSign)
      yield requestContentBuilder.addHeader(headerKey.toLowerCase(), headers.get(headerKey).get)

    requestContentBuilder.build()
  }

  /**
   * Converts nulab-oauth2 request headers to a java `RequestContent` that can be used for verification.
   */
  def convertHeaders(headers: Map[String, Seq[String]], headersToSign: List[String]): RequestContent = {
    val requestContentBuilder = new RequestContent.Builder()

    logger.debug(s"headersToSign = $headersToSign, headers= $headers")

    for {
      headerKey <- headersToSign
      headerValue <- headers.find(_._1.toLowerCase() == headerKey.toLowerCase()).flatMap(_._2.headOption)
    } yield requestContentBuilder.addHeader(headerKey.toLowerCase(), headerValue)

    requestContentBuilder.build()
  }

  def getKeyChain(key: YetuPublicKey): Keychain = {
    logger.info(s"current key: ${key.key}")
    YetuAuthorizedKeys.newKeychainFromString(key.key)
  }

  // OPTIONAL improvement: this code could to be changed to work as a challenge-response system
  // client makes one request, auth server responds with a random challenge
  // client makes a second request, signing this random challenge.
  // this is an extension provided in the java library but not part of the original joyent spec.
  // see https://github.com/adamcin/httpsig-java for details.
  def defaultChallenge = {
    new Challenge("CHALLENGE",
      Constants.DEFAULT_HEADERS,
      util.Arrays.asList(Algorithm.forName("rsa-sha256"), Algorithm.forName("rsa-sha512")))
  }

}
