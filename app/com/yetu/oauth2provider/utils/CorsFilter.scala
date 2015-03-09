package com.yetu.oauth2provider
package utils

import com.yetu.oauth2resource.filters.{ AllowAllCorsFilter, WhiteListCorsFilter }

/**
 * adds required CORS headers to chosen whiteListCORSUrls.
 *
 * Use com.yetu.oauth2resource.filters.AllowAllCorsFilter instead if you want CORS support on *all* routes.
 */
object CorsFilter extends WhiteListCorsFilter {

  val whiteListCORSUrls = List("/oauth2/access_token", "/oauth2/info", "/oauth2/access_token_implicit")

}

//object CorsFilter extends AllowAllCorsFilter

