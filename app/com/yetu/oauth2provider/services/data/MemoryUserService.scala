package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.models.DataUpdateRequest
import com.yetu.oauth2provider.oauth2.models.YetuUser
import play.api.mvc.Result
import securesocial.core.services.UserService

/**
 * TODO: implement user service properly!
 */
class MemoryUserService extends MemoryPersonService with UserService[YetuUser] with MemoryMailTokenService {

}
