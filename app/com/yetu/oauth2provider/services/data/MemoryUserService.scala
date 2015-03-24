package com.yetu.oauth2provider.services.data

import com.yetu.oauth2provider.oauth2.models.YetuUser
import securesocial.core.services.UserService

class MemoryUserService extends MemoryPersonService with UserService[YetuUser] with MemoryMailTokenService
