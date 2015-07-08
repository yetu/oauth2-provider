package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.utils.Config._
import play.api.libs.json.{ JsNull, JsValue }
import play.api.mvc.{ AnyContentAsEmpty, Result }
import play.api.test.Helpers._
import play.api.test.{ FakeHeaders, FakeRequest }

import scala.concurrent.Future

trait AuthRoutesHelper extends TestRegistry with DefaultTestVariables with BaseMethods {
  def addTestClient(clientId: String, clientSecret: String, redirectUrl: String): OAuth2Client = {

    val client = OAuth2Client(clientId = clientId,
      clientSecret = clientSecret,
      redirectURIs = List(redirectUrl),
      grantTypes = OAuth2.Defaults.defaultGrantTypes,
      clientName = s"Integration Test client $clientId",
      coreYetuClient = true)

    clientService.deleteClient(client)
    clientService.saveClient(client)
    client
  }

  def postRequest(url: String, parameters: Map[String, Seq[String]] = Map(), fakeHeaders: FakeHeaders = FakeHeaders(), sessions: List[(String, String)] = List.empty): Future[Result] = {
    val response = route(FakeRequest(POST, url, fakeHeaders, parameters).withSession(sessions: _*)).get

    log(s"response: ${contentAsString(response)}")
    log(s"response status: ${status(response)}")
    log(s"response location: ${header("Location", response)}")
    log(s"response headers: ${headers(response)}")

    response

  }

  def postRequestWithHeaderAndJsonParameters(url: String, headers: FakeHeaders = FakeHeaders(Seq("Content-type" -> Seq("application/json"))), parameters: JsValue = JsNull): Future[Result] = {
    route(FakeRequest(POST, url, headers, parameters)).get
  }

  def getRequest(url: String, headers: FakeHeaders = FakeHeaders()): Future[Result] = {
    route(FakeRequest(GET, url, headers, AnyContentAsEmpty)).get
  }
}
