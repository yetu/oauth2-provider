package com.yetu.oauth2provider.models

import com.yetu.oauth2provider.signature.models.YetuPublicKey
import play.api.libs.json.Json

case class DataListWrapper(data: List[YetuPublicKey])

object DataListWrapper {
  implicit def dataWrapperFormat = Json.format[DataListWrapper]
}
