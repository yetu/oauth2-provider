package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.{ YetuUserHelper, YetuUser }
import com.yetu.oauth2provider.utils.UUIDGenerator
import play.api.mvc.Result
import securesocial.core.BasicProfile
import securesocial.core.services.{ SaveMode, UserService }

import scala.concurrent.Future

/**
 * TODO: implement user service properly!
 */
class MemoryUserService extends MemoryPersonService with UserService[YetuUser] with MemoryMailTokenService {
}
