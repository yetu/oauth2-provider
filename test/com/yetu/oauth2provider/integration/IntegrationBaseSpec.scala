package com.yetu.oauth2provider.integration

import com.yetu.oauth2provider.base.BaseRoutesSpec
import com.yetu.oauth2provider.oauth2._

class IntegrationBaseSpec extends BaseRoutesSpec {

  val oauth2flowImplementations: List[AccessTokenRetriever] = List(AuthorizationCodeFlow, ImplicitGrantFlow, ResourceOwnerFlow, SignatureFlow)

}