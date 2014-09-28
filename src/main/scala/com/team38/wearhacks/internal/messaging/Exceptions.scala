package com.team38.wearhacks.internal.messaging

import spray.http.StatusCode
import spray.http.StatusCodes
import spray.http.HttpResponse
import spray.json._
import DefaultJsonProtocol._
import spray.http.MediaTypes._
import spray.http.HttpEntity

case class UpdateServerException(
    code: StatusCode,
    msg: String,
    info: String) extends RuntimeException(msg) with UpdateServerResponse {
  // Representation of the error returned to client
  def marshal() = {
    val obj: Map[String, JsValue] = Map(
        		"status"  -> JsNumber(code.intValue),
        		"error"   -> JsString(code.reason),
        		"message" -> JsString(msg),
        		"info"    -> JsString(info)
		)
    val json = obj.toJson
    HttpResponse(code, HttpEntity(`application/json`, json.toString))
  }
}

object UpdateServerException {
  def apply(e: Exception): UpdateServerException = new UpdateServerException(
      StatusCodes.InternalServerError,
      e.getMessage,
      e.toString)
}