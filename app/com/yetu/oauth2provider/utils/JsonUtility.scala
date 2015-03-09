package com.yetu.oauth2provider.utils

import play.api.libs.json.Json

import scala.util.Try

object JsonUtility {

  def convertPlayJsonToPlasmaJson(json: play.api.libs.json.JsValue): com.plasmaconduit.json.JsValue = {
    val maybeParsedJson: Try[com.plasmaconduit.json.JsValue] = com.plasmaconduit.json.JsonParser.parse(json.toString())
    val plasmaconduitJson = maybeParsedJson.get //Careful: may throw an error if parser is badly implemented.
    plasmaconduitJson
  }

  def convertPlasmaJsonToPlayJson(json: com.plasmaconduit.json.JsValue): play.api.libs.json.JsValue = {
    Json.parse(json.toString)
  }
}
