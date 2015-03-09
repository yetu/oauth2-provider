package com.yetu.oauth2provider.signature.services

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.iface.{ IPersonService, IPublicKeyService }
import com.yetu.oauth2provider.signature.SignatureHelper
import com.yetu.oauth2provider.signature.models.{ SignatureSyntaxException, SignatureException, YetuPublicKey, SignedRequestHeaders }
import com.yetu.oauth2provider.utils.{ Config, DateUtility }
import net.adamcin.httpsig.api.{ RequestContent, VerifyResult, DefaultVerifier, Authorization }
import net.adamcin.httpsig.ssh.jce.UserKeysFingerprintKeyId
import play.api.Logger
import play.api.mvc.Request
import play.api.mvc.Results._
import securesocial.core.AuthenticationResult.Authenticated
import securesocial.core.services.UserService
import securesocial.core.{ BasicProfile, AuthenticationResult }
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

import scala.concurrent.Future

import scala.collection.JavaConverters._
import scalaoauth2.provider._

class SignatureService[U](personService: IPersonService, keyService: IPublicKeyService) {

  lazy val logger = Logger("com.yetu.oauth2provider.signature.services.SignatureService")

  def validateRequest(implicit request: AuthorizationRequest): Future[YetuUser] = {
    parseHeaders { (signedRequestHeaders, headersToSign) =>

      val maybeUser = personService.findYetuUser(signedRequestHeaders.email)
      val maybeKey = keyService.getKey(signedRequestHeaders.email)

      val maybeSuccess = for {
        user <- maybeUser
        key <- maybeKey
      } yield verifySignature(signedRequestHeaders, user, key, headersToSign)

      maybeSuccess.getOrElse{
        logger.info(s"user[${maybeUser.isDefined}] or key[${maybeKey.isDefined}] does not exist.")
        Future.failed(SignatureException("User or key does not exist"))
      }
    }
  }

  def parseHeaders(callback: (SignedRequestHeaders, RequestContent) => Future[YetuUser])(implicit request: AuthorizationRequest): Future[YetuUser] = {

    val maybeHeaders: Option[SignedRequestHeaders] = for {
      date <- request.headers.get("date").headOption.map(_.head)
      email <- request.headers.get("email").headOption.map(_.head)
      authorization <- request.headers.get("authorization").headOption.map(_.head)
      signedRequestHeaders = SignedRequestHeaders(Authorization.parse(authorization), email, DateUtility.stringToRfcFormat(date))
    } yield signedRequestHeaders

    maybeHeaders match {
      case Some(signedRequestHeaders) => {

        val headersToSign = signedRequestHeaders.auth.getHeaders.asScala.toList
        //check that all headers defined in the "headers" authorization element actually exist:
        logger.debug(s"headers: ${request.headers}")
        logger.debug(s"headersToSign: ${headersToSign}")
        if (!headersToSign.forall(header => request.headers.get(header).isDefined)) {
          Future.failed(SignatureSyntaxException(s"headers in authorization element: $headersToSign include headers that are not defined. Received headers: ${request.headers}"))
        }

        val requestContent = SignatureHelper.convertHeaders(request.headers, headersToSign)

        callback(signedRequestHeaders, requestContent)
      }
      case _ => Future.failed(SignatureSyntaxException("Missing one header parameter [date, email, authorization], " +
        "or mismatch between actual headers and " +
        "those sent in the 'headers'(=headers used in the signature) authorization header element "))
    }
  }

  def verifySignature(signedRequestHeaders: SignedRequestHeaders, user: YetuUser, key: YetuPublicKey, requestContent: RequestContent): Future[YetuUser] = {

    val keychain = SignatureHelper.getKeyChain(key)
    val verifier = new DefaultVerifier(keychain, new UserKeysFingerprintKeyId(signedRequestHeaders.email), Config.OAuth2.signatureDateExpirationInMilliseconds)

    val result = verifier.verifyWithResult(SignatureHelper.defaultChallenge, requestContent, signedRequestHeaders.auth)

    result match {
      case VerifyResult.SUCCESS => Future.successful(user)
      case _ => {
        Future.failed(SignatureException(s"request verification failed due to: $result"))
      }
    }

  }

}

