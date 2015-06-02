package com.yetu.oauth2provider
package routes

import com.yetu.oauth2provider.base.BaseRoutesSpec
import com.yetu.oauth2provider.utils.Config._
import play.api.Logger
import play.api.test.Helpers._

class ApplicationSpec extends BaseRoutesSpec {

  s"GET $infoUrl  endpoint " must {
    "respond with 400 Badrequest when not giving any access token." in {
      val response = getRequest(infoUrl)

      status(response) mustEqual BAD_REQUEST
    }

    "respond with 401 Unauthorized when giving invalid access token at ?access_token=blabla" in {
      val url = infoUrl + "?access_token=" + "blabla"
      val response = getRequest(url)

      status(response) mustEqual UNAUTHORIZED
    }

    "respond with a user when given a valid access token " in {

      val (authInfo, token) = generateAndSaveTestVariables()

      val url = infoUrl + "?access_token=" + token.token
      val response = getRequest(url)

      status(response) mustEqual OK
      contentType(response).getOrElse("") mustEqual "application/json"
    }

  }

  s"POST $accessTokenUrl  endpoint " must {

    //initialise an oauth2 client for use during tests
    val testClient1 = addTestClient(clientId = "testClientId1", clientSecret = "testClientSecret1", redirectUrl = "http://testClientredirectUrl1.com")
    val testClient2 = addTestClient(clientId = "testClientId2", clientSecret = "testClientSecret2", redirectUrl = "http://testClientredirectUrl2.com")

    val testAuthCode1 = "Fdfasdfasfaaefasf2"
    val testAuthCode2 = "QWEQEQ"
    val (authInfo, token) = generateAndSaveTestVariables(clientId = testClient1.clientId, redirectURIs = testClient1.redirectURIs)
    authCodeAccessTokenService.saveAuthCode(testAuthCode1, authInfo)

    "respond with an access token if the correct parameters are given" in {

      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "redirect_uri" -> Seq(testClient1.redirectURIs.head),
        "client_id" -> Seq(testClient1.clientId),
        "client_secret" -> Seq(testClient1.clientSecret),
        "code" -> Seq(testAuthCode1))

      val response = postRequest(accessTokenUrl, parameters)
      log(s"${headers(response)}")
      status(response) mustEqual OK

    }

    s"respond with $BAD_REQUEST if no content given." in {
      val response = postRequest(accessTokenUrl)
      status(response) mustEqual BAD_REQUEST

    }

    s"respond with $BAD_REQUEST if not enough parameters are given" in {

      val parameters = Map(
        "grant_type" -> Seq("meh_code"),
        "bla" -> Seq("hello"))
      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual BAD_REQUEST
    }

    s"respond with $BAD_REQUEST if not enough parameters are given2" in {

      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "bla" -> Seq("hello"))
      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual BAD_REQUEST
    }

    s"respond with $UNAUTHORIZED if client secret is wrong" in {
      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "redirect_uri" -> Seq(testClient1.redirectURIs.head),
        "client_id" -> Seq(testClient1.clientId),
        "client_secret" -> Seq("wrongSecret"),
        "code" -> Seq(testAuthCode1))
      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual UNAUTHORIZED
    }

    s"respond with $UNAUTHORIZED if client id is wrong" in {
      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "redirect_uri" -> Seq(testClient1.redirectURIs.head),
        "client_id" -> Seq("blabla"),
        "client_secret" -> Seq(testClient1.clientSecret),
        "code" -> Seq(testAuthCode1))
      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual UNAUTHORIZED
    }

    s"respond with $UNAUTHORIZED if auth code is wrong" in {
      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "redirect_uri" -> Seq(testClient1.redirectURIs.head),
        "client_id" -> Seq(testClient1.clientId),
        "client_secret" -> Seq(testClient1.clientSecret),
        "code" -> Seq("wrongAuthCode"))
      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual UNAUTHORIZED
    }

    s"respond with $UNAUTHORIZED if grant type is not in the list of accepted grant types." in {

      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_CLIENT_CREDENTIALS),
        "redirect_uri" -> Seq(testClient1.redirectURIs.head),
        "client_id" -> Seq(testClient1.clientId),
        "client_secret" -> Seq(testClient1.clientSecret),
        "code" -> Seq(testAuthCode1))

      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual UNAUTHORIZED

    }

    s"respond with $UNAUTHORIZED if the wrong redirect URL is given. " in {

      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "redirect_uri" -> Seq("http://bad-malicious-evil-rogueserver.com"),
        "client_id" -> Seq(testClient1.clientId),
        "client_secret" -> Seq(testClient1.clientSecret),
        "code" -> Seq(testAuthCode1))

      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual UNAUTHORIZED
    }

    s"respond with $UNAUTHORIZED if all is correct but the wrong clientID with respect to the stored one is requesting an access token." in {

      // we have stored a testClient1 user and auth token

      // but we request it from testclient2
      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "redirect_uri" -> Seq(testClient1.redirectURIs.head),
        "client_id" -> Seq(testClient2.clientId),
        "client_secret" -> Seq(testClient2.clientSecret),
        "code" -> Seq(testAuthCode1))

      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual UNAUTHORIZED
    }
  }

  "the two endpoints together" must {

    val testClient3 = addTestClient(clientId = "testClientId3", clientSecret = "testClientSecret3", redirectUrl = "http://testClientredirectUrl3.com")
    val testAuthCode3 = "EKFAFHILASKVSADH"
    val (authInfo, token) = generateAndSaveTestVariables(clientId = testClient3.clientId, redirectURIs = testClient3.redirectURIs)
    authCodeAccessTokenService.saveAuthCode(testAuthCode3, authInfo)

    "work and give back a user" in {
      val parameters = Map(
        "grant_type" -> Seq(GRANT_TYPE_AUTHORIZATION_CODE),
        "redirect_uri" -> Seq(testClient3.redirectURIs.head),
        "client_id" -> Seq(testClient3.clientId),
        "client_secret" -> Seq(testClient3.clientSecret),
        "code" -> Seq(testAuthCode3))

      val response = postRequest(accessTokenUrl, parameters)
      status(response) mustEqual OK
      val content = contentAsString(response)
      Logger.info(content)
      val url = infoUrl + "?access_token=" + token.token
      val responseInfo = getRequest(url)

      status(responseInfo) mustEqual OK
      contentType(responseInfo).getOrElse("") mustEqual "application/json"

    }

  }

}

/*

1.
client makes first request to http://localhost:9005/oauth2/authorize?scope=basic&client_id=homescreen&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fauthenticate%2Fgoogle&response_type=code&state=882b757d-7ace-4e0f-8742-644d8ff61f44
auth server receives /oauth2/authorize?scope=basic&client_id=homescreen&redirect_uri=http%3A%2F%2Flocalhost%3A9000%2Fauthenticate%2Fgoogle&response_type=code&state=744b4c81-9881-4488-9ad1-d9debbc92200

2.
user authenticates

3.
authprovider saves user and auth code (saveAuthCode code=vRFJ4M76vH user=...) , then redirect to client with auth code & state

4.
client makes POST request with:
POST /oauth2/access_token
AnyContentAsFormUrlEncoded(Map(
 redirect_uri -> Seq(http://localhost:9000/authenticate/google),
 client_id -> Seq(homescreen),
 code -> Seq(FDdrVgoQo2),
 client_secret -> Seq(rjW28HMq5ip6SNKhKKinPtyS5ECcjFuNeTRAPEVQDiFGWkTntLWo8Zh4bsZA),
 grant_type -> Seq(authorization_code)
))

5.
auth server responds with {"token_type":"Bearer","access_token":"_gccj-nas8CC5xxTA8gdVuRYgREOAFgO","expires_in":50000,"refresh_token":"lqAOsbszXT7N2XhxE0A-GTwt7H5dCgh3"}

6.
client can use the access token to make a /oauth2/info?access_token=_gccj-nas8CC5xxTA8gdVuRYgREOAFgO request and gets

e.g. {"username":"joe.schaul@yetu.com"} back.


*/

