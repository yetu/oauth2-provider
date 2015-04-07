package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.registry.{ TestRegistry, IntegrationTestRegistry }
import com.yetu.oauth2provider.services.data.iface.IPersonService
import play.api.libs.json.Json
import securesocial.core.services.SaveMode

/*
 * This test class is extended below to run the same tests against the in-memory and the LDAP implementations.
 */
abstract class BaseUserContactDataServiceSpec extends DataBaseSpec {

  s"The [$databaseImplementationName] Contact Service" must {

    "update the user contact with full data " in {

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

      //DELETE IF USER EXISTS
      personService.deleteUser(testUser.identityId.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)

      personService.updateUserProfile(testUser, updateDataObject)

      val retrieved = personService.findYetuUser(testUser.userId)
      retrieved.isDefined mustBe true
      //Basic Data
      retrieved.get.firstName mustEqual (jsonUpdateData \ "firstName").as[String]
      retrieved.get.lastName mustEqual (jsonUpdateData \ "lastName").as[String]
      retrieved.get.fullName mustEqual ((jsonUpdateData \ "firstName").as[String] + " " + (jsonUpdateData \ "lastName").as[String])

      //Contact Data
      retrieved.get.contactInfo.get.country.get mustEqual (jsonUpdateData \ "contactInfo" \ "country").as[String]
      retrieved.get.contactInfo.get.street.get mustEqual (jsonUpdateData \ "contactInfo" \ "street").as[String]
      retrieved.get.contactInfo.get.houseNumber.get mustEqual (jsonUpdateData \ "contactInfo" \ "houseNumber").as[String]
      retrieved.get.contactInfo.get.postalCode.get mustEqual (jsonUpdateData \ "contactInfo" \ "postalCode").as[String]
      retrieved.get.contactInfo.get.city.get mustEqual (jsonUpdateData \ "contactInfo" \ "city").as[String]
      retrieved.get.contactInfo.get.mobile.get mustEqual (jsonUpdateData \ "contactInfo" \ "mobile").as[String]
      retrieved.get.contactInfo.get.homePhone.get mustEqual (jsonUpdateData \ "contactInfo" \ "homePhone").as[String]
      retrieved.get.contactInfo.get.fax.get mustEqual (jsonUpdateData \ "contactInfo" \ "fax").as[String]
      retrieved.get.contactInfo.get.chat.get mustEqual (jsonUpdateData \ "contactInfo" \ "chat").as[String]
    }

    "update the user contact with partial data " in {

      val updateData = """{
                         |    "lastName": "testSurname",
                         |    "contactInfo": {
                         |        "city": "testCity",
                         |        "mobile": "1111111"
                         |    }
                         }""".stripMargin

      val jsonUpdateData = Json parse updateData
      val updateDataObject = jsonUpdateData.as[DataUpdateRequest]

      //DELETE IF USER EXISTS
      personService.deleteUser(testUser.identityId.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)

      personService.updateUserProfile(testUser, updateDataObject)

      val retrieved = personService.findYetuUser(testUser.userId)
      retrieved.isDefined mustBe true

      retrieved.get.lastName mustEqual (jsonUpdateData \ "lastName").as[String]
      retrieved.get.fullName mustEqual testUser.firstName + " " + (jsonUpdateData \ "lastName").as[String]
      retrieved.get.contactInfo.get.city.get mustEqual (jsonUpdateData \ "contactInfo" \ "city").as[String]
      retrieved.get.contactInfo.get.mobile.get mustEqual (jsonUpdateData \ "contactInfo" \ "mobile").as[String]
      retrieved.get.contactInfo.get.homePhone.isEmpty mustBe true
      retrieved.get.contactInfo.get.houseNumber.isEmpty mustBe true
      retrieved.get.contactInfo.get.fax.isEmpty mustBe true
      retrieved.get.contactInfo.get.country.isEmpty mustBe true
      retrieved.get.contactInfo.get.street.isEmpty mustBe true
      retrieved.get.contactInfo.get.postalCode.isEmpty mustBe true
      retrieved.get.contactInfo.get.chat.isEmpty mustBe true
    }

    "update the user contact with no data " in {

      val updateData = """{


                         }""".stripMargin

      val jsonUpdateData = Json parse updateData

      val updateDataObject = jsonUpdateData.as[DataUpdateRequest]

      //DELETE IF USER EXISTS
      personService.deleteUser(testUser.identityId.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)

      personService.updateUserProfile(testUser, updateDataObject)

      val retrieved = personService.findYetuUser(testUser.userId)
      retrieved.isDefined mustBe true

      retrieved.get.contactInfo.get.city.isEmpty mustBe true
      retrieved.get.contactInfo.get.mobile.isEmpty mustBe true
      retrieved.get.contactInfo.get.homePhone.isEmpty mustBe true
      retrieved.get.contactInfo.get.houseNumber.isEmpty mustBe true
      retrieved.get.contactInfo.get.fax.isEmpty mustBe true
      retrieved.get.contactInfo.get.country.isEmpty mustBe true
      retrieved.get.contactInfo.get.street.isEmpty mustBe true
      retrieved.get.contactInfo.get.postalCode.isEmpty mustBe true
      retrieved.get.contactInfo.get.chat.isEmpty mustBe true
    }

  }
}

class LDAPUserContactDataServiceITSpec extends BaseUserContactDataServiceSpec with IntegrationTestRegistry

//TODO: implement the same functionality in-memory
//class MemoryUserContactDataServiceSpec extends BaseUserContactDataServiceSpec with TestRegistry