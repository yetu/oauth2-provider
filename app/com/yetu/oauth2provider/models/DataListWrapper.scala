package com.yetu.oauth2provider.models

import com.yetu.oauth2provider.signature.models.YetuPublicKey
import play.api.libs.json.{ Format, Json }

case class DataListWrapper(data: List[YetuPublicKey])

object DataListWrapper {
  implicit val dataWrapperFormat: Format[DataListWrapper] = Json.format[DataListWrapper]
}
