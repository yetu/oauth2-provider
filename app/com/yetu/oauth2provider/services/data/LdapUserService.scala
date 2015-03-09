package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.data.ldap.LdapDAO
import com.yetu.oauth2provider.oauth2.models.YetuUser
import securesocial.core.services.UserService

/**
 * combines a PersonService with a MailTokenService to adhere to securesocial's UserService interface.
 */
class LdapUserService(dao: LdapDAO) extends LdapPersonService(dao) with MemoryMailTokenService with UserService[YetuUser]
