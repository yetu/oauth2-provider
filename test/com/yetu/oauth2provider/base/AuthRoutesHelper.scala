package com.yetu.oauth2provider.base

import com.yetu.oauth2provider.base.DefaultTestVariables
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.oauth2.models.OAuth2Client
import com.yetu.oauth2provider.registry.TestRegistry
import com.yetu.oauth2provider.utils.Config._
import com.yetu.oauth2resource.utils.RoutesHelper
import org.scalatestplus.play._
import play.api.libs.json.{ JsNull, JsValue }
import play.api.mvc.{ Headers, AnyContentAsEmpty, Result }
import play.api.test.Helpers._
import play.api.test.{ FakeApplication, FakeHeaders, FakeRequest }

import scala.concurrent.Future

trait AuthRoutesHelper extends TestRegistry with DefaultTestVariables with BaseMethods {
  def addTestClient(clientId: String, clientSecret: String, redirectUrl: String): OAuth2Client = {

    val client = OAuth2Client(clientId = clientId,
      clientSecret = clientSecret,
      scopes = Some(List(SCOPE_ID)),
      redirectURIs = List(redirectUrl),
      grantTypes = OAuth2.Defaults.defaultGrantTypes,
      clientName = s"Integration Test client $clientId", coreYetuClient = true)

    clientService.deleteClient(client)
    clientService.saveClient(client)
    client
  }

  def postRequest(url: String, parameters: Map[String, Seq[String]] = Map(), fakeHeaders: FakeHeaders = FakeHeaders()): Future[Result] = {
    val response = route(FakeRequest(POST, url, fakeHeaders, parameters)).get

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
