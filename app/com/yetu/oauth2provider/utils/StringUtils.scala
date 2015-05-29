package com.yetu.oauth2provider
package utils

object StringUtils {

  /**
   * Method returns true if a String contains text
   * @param str Value that will be checked
   * @return
   */
  def isFull(str: Option[String]): Boolean = {
    str match {
      case Some(value) =>
        value.trim.length > 0
      case None =>
        false
    }
  }

  /**
   * Method returns true if any of the giving Strings is empty, return false if all are string
   * @param strs Sequence of string
   * @return
   */
  def isAnyEmpty(strs: Option[String]*): Boolean = {
    val newSequence = strs.filter(str => isFull(str))
    newSequence.length != strs.length
  }

  def toHashmark(url: String, queryString: Map[String, Seq[String]] = Map.empty) = {
    import java.net.URLEncoder
    val fullUrl = url + Option(queryString).filterNot(_.isEmpty).map { params =>
      (if (url.contains("#")) "&" else "#") + params.toSeq.flatMap { pair =>
        pair._2.map(value => (pair._1 + "=" + URLEncoder.encode(value, "utf-8")))
      }.mkString("&")
    }.getOrElse("")
    fullUrl
  }

  /**
   * Extract the subdomain as .domain.com from a given request.host property
   * @param host Request host param
   * @return string with the subdomain
   */
  def subdomain(host: String): String = {

    var domain: String = host
    if ("\\.".r.findAllMatchIn(domain).length >= 2) {

      if (host.contains(":")) {
        domain = host.substring(0, host.indexOf(":"))
      }

      val pieces = domain.split("\\.")
      if (pieces.length >= 3) {
        domain = "." + List(pieces(pieces.length - 2), pieces(pieces.length - 1)).mkString(".")
      }
    }

    domain
  }

}
