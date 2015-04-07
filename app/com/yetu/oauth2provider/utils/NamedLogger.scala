package com.yetu.oauth2provider.utils

import play.api.Logger

trait NamedLogger {

  /**
   * Will result in a logger with the same name as the class that implements/extends this trait
   */
  val logger = Logger(this.getClass)
}
