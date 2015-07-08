package com.yetu.oauth2provider
package oauth2
package services

import com.yetu.oauth2provider.oauth2.models.{ ClientScopes, YetuUser }
import com.yetu.oauth2provider.utils.Config._
import com.yetu.oauth2provider.utils.DateUtility
import com.yetu.oauth2resource.model.User

class ScopeService {

  def getInfoByScope(user: YetuUser, scopeString: String): Option[User] = {

    val scopes: List[String] = scopeString.split(' ').toList

    val idScopes = List(SCOPE_ID,
      SCOPE_BASIC, SCOPE_EVENTS, SCOPE_CONTACT,
      SCOPE_PASSWORD, SCOPE_REGISTRATION_INFO, SCOPE_HOUSEHOLD_READ,
      SCOPE_HOUSEHOLD_WRITE, SCOPE_HOUSEHOLD_GENERATE, SCOPE_CONTROLCENTER)

    val basicScopes = List(
      SCOPE_BASIC, SCOPE_EVENTS, SCOPE_CONTACT,
      SCOPE_PASSWORD, SCOPE_REGISTRATION_INFO, SCOPE_HOUSEHOLD_READ,
      SCOPE_HOUSEHOLD_WRITE, SCOPE_HOUSEHOLD_GENERATE, SCOPE_CONTROLCENTER)

    val contactScopes = List(SCOPE_CONTACT, SCOPE_CONTROLCENTER)

    val registrationScopes = List(SCOPE_REGISTRATION_INFO, SCOPE_CONTROLCENTER)

    scopes.find(idScopes.contains(_)).map { someValidIdScope =>
      User(
        userId = scopes.find(idScopes.contains(_)).map(_ => user.userId),
        firstName = scopes.find(basicScopes.contains(_)).map(_ => user.firstName.get),
        lastName = scopes.find(basicScopes.contains(_)).map(_ => user.lastName.get),
        email = scopes.find(basicScopes.contains(_)).map(_ => user.email.get),
        contactInfo = scopes.find(contactScopes.contains(_)).flatMap(_ => user.contactInfo),
        registrationDate = scopes.find(registrationScopes.contains(_)).flatMap(_ => user.registrationDate.map(date => DateUtility.dateToUtcString(date)))
      )
    }

  }

  /**
   * Until we change the nuulab library to allow for a list of scopes, we take the first available scope.
   * TODO: change nulab-scala-provider library
   */
  def getFirstScope(scopes: Option[List[String]]): Option[String] = {
    scopes match {
      case None        => None
      case Some(scope) => Some(scope.head)
    }
  }

  def getScopeFromPermission(clientPermission: Option[ClientScopes]): List[String] = {
    clientPermission match {
      case None => List.empty
      case Some(permission) => permission.scopes match {
        case None         => List.empty
        case Some(scopes) => scopes
      }
    }
  }
}
