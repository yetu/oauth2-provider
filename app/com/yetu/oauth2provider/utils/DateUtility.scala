package com.yetu.oauth2provider
package utils

import java.text.SimpleDateFormat
import java.util.{ Date, TimeZone }

import net.adamcin.httpsig.api.RequestContent
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants.MILLIS_PER_SECOND
import org.joda.time.format.DateTimeFormat

object DateUtility {

  private val UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"
  private val utcDateFormat = new SimpleDateFormat(UTC_FORMAT)
  private val utcDateTimeFormatter = DateTimeFormat.forPattern(UTC_FORMAT)

  def utcStringToDate(date: String): Date = {
    utcDateFormat.parse(date)
  }

  def utcStringToDateTime(date: String): DateTime = {
    utcDateTimeFormatter.parseDateTime(date)
  }

  def rfcFormatToStringWithUTC(date: Date): String = {
    val dateFormat = new SimpleDateFormat(RequestContent.DATE_FORMAT_RFC1123)
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    dateFormat.format(date)
  }

  def stringToRfcFormat(str: String): Date = {
    val dateFormat = new SimpleDateFormat(RequestContent.DATE_FORMAT_RFC1123)
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    dateFormat.parse(str)
  }

  // java Date <---> String

  private val LDAP_DATE_FORMAT = "yyyyMMddHHmmss"
  private val ldapDateFormat: SimpleDateFormat = new SimpleDateFormat(LDAP_DATE_FORMAT)

  def dateToString(date: Date): String = {
    new SimpleDateFormat("yyyy.MM.dd").format(date)
  }

  def LDAPStringToDate(value: String): Date = {
    ldapDateFormat.parse(value)
  }

  // joda DateTime <---> String

  private val LDAP_CUSTOM_DATE_FORMAT = "yyyyMMddHHmmss.SSS Z"
  private val ldapDateTimeFormatter = DateTimeFormat.forPattern(LDAP_CUSTOM_DATE_FORMAT)

  def DateTimeToString(dateTime: DateTime): String = {
    dateTime.toString(LDAP_CUSTOM_DATE_FORMAT)
  }

  def DateTimeFromString(dateTime: String): DateTime = {
    ldapDateTimeFormatter.parseDateTime(dateTime)
  }

  def unixSecondsNow(): Long = {
    System.currentTimeMillis() / MILLIS_PER_SECOND
  }

  def unixSecondsOf(date: DateTime): Long = {
    date.getMillis / MILLIS_PER_SECOND
  }

  def unixSecondsDefaultExpiration(): Long = {
    unixSecondsOf(DateTime.now().plusSeconds(Config.OAuth2.accessTokenExpirationInSeconds))
  }

}
