package com.yetu.oauth2provider.signature.services

import com.yetu.oauth2provider.oauth2.models.YetuUser
import com.yetu.oauth2provider.services.data.interface.{ IPersonService, IPublicKeyService }
import com.yetu.oauth2provider.signature.SignatureHelper
import com.yetu.oauth2provider.signature.models.{ SignatureException, SignatureSyntaxException, SignedRequestHeaders, YetuPublicKey }
import com.yetu.oauth2provider.utils.{ Config, DateUtility }
import net.adamcin.httpsig.api.{ Authorization, DefaultVerifier, RequestContent, VerifyResult }
import net.adamcin.httpsig.ssh.jce.UserKeysFingerprintKeyId
import play.api.Logger

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider._

class SignatureService[U](personService: IPersonService, keyService: IPublicKeyService) {

  lazy val logger = Logger("com.yetu.oauth2provider.signature.services.SignatureService")

  def validateRequest(implicit request: AuthorizationRequest): Future[Option[YetuUser]] = {
    parseHeaders { (signedRequestHeaders, headersToSign) =>
      for {
        user <- personService.findYetuUser(signedRequestHeaders.email)
        key <- keyService.getKeyF(signedRequestHeaders.email)
        validate <- verifySignature(signedRequestHeaders, user, key, headersToSign)
      } yield validate
    }
  }

  def parseHeaders(callback: (SignedRequestHeaders, RequestContent) => Future[Option[YetuUser]])(implicit request: AuthorizationRequest): Future[Option[YetuUser]] = {

    val maybeHeaders: Option[SignedRequestHeaders] = for {
      date <- request.headers.get("date").map(_.head)
      email <- request.headers.get("email").map(_.head)
      authorization <- request.headers.get("authorization").map(_.head)
      signedRequestHeaders = SignedRequestHeaders(Authorization.parse(authorization), email, DateUtility.stringToRfcFormat(date))
    } yield signedRequestHeaders

    maybeHeaders match {
      case Some(signedRequestHeaders) =>

        val headersToSign = signedRequestHeaders.auth.getHeaders.asScala.toList
        //check that all headers defined in the "headers" authorization element actually exist:
        logger.debug(s"headers: ${request.headers}")
        logger.debug(s"headersToSign: $headersToSign")
        if (!headersToSign.forall(header => request.headers.get(header).isDefined)) {
          Future.failed(SignatureSyntaxException(s"headers in authorization element: $headersToSign include headers that are not defined. Received headers: ${request.headers}"))
        }

        val requestContent = SignatureHelper.convertHeaders(request.headers, headersToSign)
        callback(signedRequestHeaders, requestContent)

      case _ => Future.failed(SignatureSyntaxException("Missing one header parameter [date, email, authorization], " +
        "or mismatch between actual headers and " +
        "those sent in the 'headers'(=headers used in the signature) authorization header element "))
    }
  }

  def verifySignature(signedRequestHeaders: SignedRequestHeaders, user: Option[YetuUser], key: Option[YetuPublicKey], requestContent: RequestContent): Future[Option[YetuUser]] = {

    val keychain = SignatureHelper.getKeyChain(key.getOrElse(YetuPublicKey("")))

    val verifier = new DefaultVerifier(keychain,
      new UserKeysFingerprintKeyId(signedRequestHeaders.email),
      Config.OAuth2.signatureDateExpirationInMilliseconds)

    val result = verifier.verifyWithResult(SignatureHelper.defaultChallenge,
      requestContent,
      signedRequestHeaders.auth)

    result match {
      case VerifyResult.SUCCESS => Future.successful(user)
      case _                    => Future.failed(SignatureException(s"request verification failed due to: $result"))
    }
  }

}

