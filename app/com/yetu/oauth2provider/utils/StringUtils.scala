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

}
