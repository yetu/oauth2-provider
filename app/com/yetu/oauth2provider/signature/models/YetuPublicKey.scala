package com.yetu.oauth2provider.signature.models

import play.api.libs.json.Json

case class YetuPublicKey(key: String)

object YetuPublicKey {
  implicit val publicKeyFormat = Json.format[YetuPublicKey]
}
