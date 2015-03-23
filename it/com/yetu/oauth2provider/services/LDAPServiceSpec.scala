package com.yetu.oauth2provider.services

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ClientPermission, IdentityId, OAuth2Client, YetuUser}
import com.yetu.oauth2provider.registry.{IntegrationTestRegistry}
import com.yetu.oauth2provider.signature.models.YetuPublicKey
import com.yetu.oauth2provider.testdata.DefaultTestVariables
import com.yetu.oauth2provider.utils.DateUtility._
import org.joda.time.DateTime
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Json
import securesocial.core.services.SaveMode
import securesocial.core.{AuthenticationMethod, PasswordInfo}

/**
 * These tests extend the IntegrationTestRegistry and use the real configured LDAP
 *
 * requires a connection to LDAP with valid credentials:
 *
 * ldap {
 *   username="cn=..."
 *   password="..."
 *   hostname="..."
 *   port=1389
 * }
 *
 */
class LDAPServiceSpec extends PlaySpec with OneAppPerSuite with IntegrationTestRegistry with DefaultTestVariables {

  "The LDAP user service" must {
    "store and retrieve a user " in {
      personService.deleteUser(testUser.identityId.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      val retrieved = personService.findYetuUser(testUser.userId)

      retrieved.isDefined mustBe true

      retrieved.get.email mustEqual testUser.email
      retrieved.get.firstName mustEqual testUser.firstName
      retrieved.get.passwordInfo mustEqual testUser.passwordInfo
      personService.deleteUser(testUser.identityId.userId)
    }

    "store and retrieve a user with registration date " in {
      //registration date is automatically generated in LDAP

      personService.deleteUser(testUser.identityId.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      val retrieved = personService.findYetuUser(testUser.userId)

      retrieved.isDefined mustBe true

      retrieved.get.registrationDate.get must not be None
      dateToString(retrieved.get.registrationDate.get) mustEqual dateToString(DateTime.now().toDate)
    }

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

    "delete, store and retrieve a client with one of each redirects, scope and grantType " in {
      val client = OAuth2Client("2223", "secret", List("http://a"), Some(List("code")), Some(List("scope1")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)
      clientService.saveClient(client)
      val retrieved = clientService.findClient(client.clientId)

      retrieved.get mustEqual client
    }

    "delete, store and retrieve a client with multiple attributes " in {
      val client = OAuth2Client("2224", "secret", List("http://a", "http://b"), Some(List("code", "bla")), Some(List("scope1", "scope2")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)
      clientService.saveClient(client)

      val retrieved = clientService.findClient(client.clientId)
      retrieved.get mustEqual client
    }

    "delete, store and retrieve a client with some attribute being None (at the moment all attributes are mandatory, ignoring test case)" ignore {
      val client = OAuth2Client("2225", "secret", List("http://a"), Some(List("code", "bla")), Some(List("scope1", "scope2")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)
      clientService.saveClient(client)

      val retrieved = clientService.findClient(client.clientId)
      retrieved.get mustEqual client
    }

    "store a client multiple time must fail gracefully if ignoreEntryAlreadyExists = true " in {
      val client = OAuth2Client("2224", "secret", List("http://a", "http://b"), Some(List("code", "bla")), Some(List("scope1", "scope2")), "clientName", coreYetuClient = true)
      clientService.deleteClient(client)
      clientService.saveClient(client, ignoreEntryAlreadyExists = true)
      clientService.saveClient(client, ignoreEntryAlreadyExists = true)

      //must not throw an exception.

    }

    " return None if client not in database " in {
      val retrieved = clientService.findClient("65678987654567898765")
      retrieved mustBe None
      //must not throw an exception.

    }

    "delete, store and retrieve a client with clientID as String " in {
      val client = OAuth2Client("delpes", "secret", List("http://a"), Some(List("code")), Some(List("scope1")), "clientName", coreYetuClient = true)

      clientService.deleteClient(client)

      clientService.saveClient(client)
      val retrieved = clientService.findClient(client.clientId)

      retrieved.get mustEqual client
    }

    "delete, store and retrieve a permissions " in {

      //DELETE THE USER IF EXIST
      personService.deleteUser(testUser.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)

      val clientPermission = ClientPermission("123456", Some(List("scope1")))

      permissionService.deletePermission(testUser.email.get, clientPermission.clientId)
      permissionService.savePermission(testUser.email.get, clientPermission, true)
      val retrieved = permissionService.findPermission(testUser.email.get, clientPermission.clientId)
      retrieved.get mustEqual clientPermission

      personService.deleteUser(testUser.identityId.userId)

    }

    "change password of user " in {

      personService.deleteUser(testUser.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)
      val newUserPassObject = testUser.copy(passwordInfo = Some(PasswordInfo("bcrypt", "$2a$10$huRtPOgtcSMvaYiznS3IG.8elJVBvSCDXUD11EXK6FLZqw5nL7iiO", None)))
      personService.save(newUserPassObject.toBasicProfile, SaveMode.PasswordChange)

      val retrieved = personService.findYetuUser(newUserPassObject.userId)

      retrieved.get.passwordInfo mustEqual newUserPassObject.passwordInfo
      retrieved.get.passwordInfo must not be testUser.passwordInfo

      personService.deleteUser(testUser.identityId.userId)

    }

  }

  "The LDAP public key service" must {
    val testKey = YetuPublicKey("rsa-ssh ASDFDGHEGWEAFS...")

    "store and retrieve a key" in {

      personService.deleteUser(testUser.userId)
      personService.save(testUser.toBasicProfile, SaveMode.SignUp)

      publicKeyService.storeKey(testUser.userId, testKey)
      publicKeyService.getKey(testUser.userId).value mustEqual (testKey)

      personService.deleteUser(testUser.userId)
    }

  }

}
