package com.yetu.oauth2provider.oauth2

import java.io.File
import java.util.{ TimeZone, GregorianCalendar }

import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.signature.SignatureHelper._
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.utils.Config._
import com.yetu.oauth2provider.utils.{ Config, DateUtility }
import net.adamcin.httpsig.api.{ Signer, DefaultKeychain, RequestContent }
import net.adamcin.httpsig.ssh.bc.PEMUtil
import net.adamcin.httpsig.ssh.jce.UserKeysFingerprintKeyId
import play.api.test.FakeHeaders
import play.api.test.Helpers._
import securesocial.core.services.SaveMode

trait SignatureFlow extends AccessTokenRetriever {

  // public key belonging to /test/resources/test_private_rsa_key
  val testKey = YetuPublicKey("ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC3m8CfVnanBBDQOb5kCcIxyg9e0rQaoy0SqhcH+UN5crf18aAjjKqxxqKYdJMXMqP+KxgE9nx7J2kVh/7QonEIjHPoLwUMYLGz/+beJEx8vD2sfIMFTFaIX2r9satmEHHsKyKKhBiBJrG+Wq9CyT1GM03ndInLPV6U3IZxg5GDmTkf2UuS6FpaW4xK91vLE/sUeo6+w1zQe1OfPKzdbivmI6RoZohHrz9sQLQMaT4twfp0LJT/xidrcKEFQ4UUn2YB9+CiXhbPgWM/Q/8KfS3kOK4WbbLAWNqQaammiddydj7ZOT3ZtLRKROvzJN/4gGRRLStS8NLchA19SrxD2ZGN test@test.test")

  def setupUser() = {
    prepareClientAndUser(coreYetuClient = true)
    personService.deleteUser(testUser.userId)
    personService.save(testUser.toBasicProfile, SaveMode.SignUp)

    publicKeyService.storeKey(testUser.userId, testKey)
  }

  def setupKey() = {
    publicKeyService.storeKey(testUser.userId, testKey)
  }

  //java weirdness: must be 'def', not val.
  def calendar: GregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"))

  private def getAuthorization(requestContent: RequestContent) = {

    val keychain: DefaultKeychain = new DefaultKeychain()
    keychain.add(PEMUtil.readKey(new File(getClass.getResource("/test_private_rsa_key").getPath), null))

    val signer: Signer = new Signer(keychain, new UserKeysFingerprintKeyId("test@test.test"))

    signer.sign(requestContent)
  }

  def signedHeaders(headers: FakeHeaders, headersToSign: List[String]) = {
    val authorization = getAuthorization(convertHeaders(headers, headersToSign))
    FakeHeaders(headers.data ++ Seq("Authorization" -> Seq(authorization.getHeaderValue())))
  }

  def validHeaders = signedHeaders(FakeHeaders(Seq(
    "date" -> Seq(DateUtility.rfcFormatToStringWithUTC(calendar.getTime)),
    "email" -> Seq("test@test.test")
  )), List("date", "email"))

  def validParams: Map[String, Seq[String]] = {
    Map("grant_type" -> Seq(GRANT_TYPE_SIGNATURE),
      "client_id" -> Seq(integrationTestClientId),
      "client_secret" -> Seq(integrationTestSecret))
  }

  override def getAccessToken() = {
    prepareClientAndUser()
    setupUser()

    val response = postRequest(accessTokenUrl, validParams, validHeaders)
    val accessToken = (contentAsJson(response) \ ("access_token")).as[String]
    accessToken
  }
}

object SignatureFlow extends SignatureFlow
