package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.base.DataServiceBaseSpec
import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Seconds, Span }
import play.api.libs.json.Json
import securesocial.core.services.SaveMode

import scala.concurrent.ExecutionContext.Implicits.global

/*
 * This test class is extended below to run the same tests against the in-memory and the LDAP implementations.
 */
abstract class BaseUserContactDataServiceSpec extends DataServiceBaseSpec with ScalaFutures {

  s"The [$databaseImplementationName] Contact Service" must {

    "update the user contact with full data" in {

      val updateData = """{
                         |    "firstName": "testFirstName",
                         |    "lastName": "testLastName",
                         |    "contactInfo": {
                         |        "country": "Germany",
                         |        "street": "testStreet",
                         |        "houseNumber": "testHouseNumber",
                         |        "postalCode": "1111111",
                         |        "city": "testCity",
                         |        "mobile": "1111111",
                         |        "homePhone": "111111",
                         |        "fax": "1111111",
                         |        "chat": "testChat"
                         |    }
                         |}""".stripMargin

      val jsonUpdateData = Json parse updateData
      val updateDataObject = jsonUpdateData.as[DataUpdateRequest]

      val modifiedUser = testUser.copyUser(
        firstName = updateDataObject.firstName,
        lastName = updateDataObject.lastName,
        fullName = Some(updateDataObject.firstName.get + " " + updateDataObject.lastName.get),
        contactInfo = updateDataObject.contactInfo)

      val result = for {
        delete <- personService.deleteUser(testUser.userId)
        save <- personService.save(testUser, SaveMode.SignUp)
        update <- personService.updateUser(modifiedUser)
        find <- personService.findUser(testUser.userId)
      } yield find

      whenReady(result, timeout(Span(5, Seconds))) {
        retrieved =>
          retrieved.isDefined mustBe true
          retrieved.get.firstName mustEqual Some((jsonUpdateData \ "firstName").as[String])
          retrieved.get.lastName mustEqual Some((jsonUpdateData \ "lastName").as[String])
          retrieved.get.fullName mustEqual Some((jsonUpdateData \ "firstName").as[String] + " " + (jsonUpdateData \ "lastName").as[String])

        //TODO: implement contact info

      }
    }

  }
}

class APIUserContactDataServiceITSpec extends BaseUserContactDataServiceSpec with IntegrationTestRegistry

class MemoryUserContactDataServiceSpec extends BaseUserContactDataServiceSpec with TestRegistry