package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.oauth2.AuthorizationCodeFlow
import com.yetu.oauth2provider.utils.Config._
import com.yetu.oauth2resource.model.ContactInfo
import play.api.libs.json.{ JsError, JsSuccess }
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.Results.NotFound

import scala.concurrent.Future

class IntegrationUserDataSpec extends IntegrationBaseSpec with AuthorizationCodeFlow {

  //When scope is basic,there must not be "contactInfo" in json result
  s"GET user information based on basic scope" must {
    "return information based on basic scope (e.g. firstName,..)" in {

      val accessToken = oauth2AccessTokenDance(List(SCOPE_BASIC), coreYetuClient = true)
      val url = s"$infoUrl?access_token=$accessToken"
      val response = getRequest(url)

      status(response) mustEqual 200
      (contentAsJson(response) \ "firstName").validate[String] mustEqual testUser.firstName
      (contentAsJson(response) \ "lastName").validate[String] mustEqual testUser.lastName
      (contentAsJson(response) \ "contactInfo").validate[ContactInfo] match {
        case JsError(_)         => None mustEqual None
        case JsSuccess(json, _) => json.country.getOrElse(None) mustEqual None
      }
    }

    //When scope is contact, there must be "contactInfo" in json result even if there is no field regarding
    //contact information in LDAP for the user.
    //TODO: upon implementing contact Info, make LDAP and inMemory services consistent and add this test back in.
    "return information based on contact scope (e.g. street, postalcode,..)" ignore {

      val modifiedUser = testUser.copyUser(
        firstName = dataUpdateRequest.firstName,
        lastName = dataUpdateRequest.lastName,
        contactInfo = dataUpdateRequest.contactInfo)

      personService.updateUser(modifiedUser)

      val response: Future[Result] = for {

        user <- personService.findUser(testUser.userId)
        result <- user match {
          case Some(u) =>

            val (inf, tok) = generateAndSaveTestVariables(SCOPE_CONTACT, u)
            val accessToken = oauth2AccessTokenDance(List(SCOPE_CONTACT), coreYetuClient = true, deleteSaveTestUser = false)

            val URL = s"$infoUrl?access_token=$accessToken"
            getRequest(URL)

          case _ => Future.successful(NotFound)
        }

      } yield result

      status(response) mustEqual 200
      log(contentAsJson(response).toString())
      (contentAsJson(response) \ "firstName").validate[String] mustEqual testUser.firstName
      (contentAsJson(response) \ "lastName").validate[String] mustEqual testUser.lastName
      (contentAsJson(response) \ "contactInfo").validate[ContactInfo] match {
        case JsError(_)         => None mustEqual None
        //as we add country as a default contact information to testUser, so we could expect that
        // we have a "co" value
        case JsSuccess(json, _) => json.country must be ('defined)
      }
    }

    "return information is the access token was generated with multiple scopes." in {

      val multiScope = List(SCOPE_BASIC, SCOPE_HOUSEHOLD_READ)

      val accessToken = oauth2AccessTokenDance(multiScope, coreYetuClient = true)
      val url = s"$infoUrl?access_token=$accessToken"
      val response = getRequest(url)

      status(response) mustEqual 200
      (contentAsJson(response) \ "firstName").validate[String] mustEqual testUser.firstName
      (contentAsJson(response) \ "lastName").validate[String] mustEqual testUser.lastName

    }
  }

}