package com.yetu.oauth2provider.oauth2

import com.yetu.oauth2provider.oauth2.services.{ ImplicitGrantFlowHandler, SignatureHandler }
import scalaoauth2.provider.TokenEndpoint
import com.yetu.oauth2provider.utils.Config.{ GRANT_TYPE_SIGNATURE, GRANT_TYPE_TOKEN }

class OAuth2TokenEndpoint[U](signatureHandler: SignatureHandler[U], implicitGrantFlowHandler: ImplicitGrantFlowHandler[U]) extends TokenEndpoint {

  override val handlers = TokenEndpoint.handlers ++
    Map(GRANT_TYPE_SIGNATURE -> signatureHandler) ++
    Map(GRANT_TYPE_TOKEN -> implicitGrantFlowHandler)
}

