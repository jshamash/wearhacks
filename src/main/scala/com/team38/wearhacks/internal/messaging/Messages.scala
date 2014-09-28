package com.team38.wearhacks.internal.messaging

import com.team38.wearhacks.internal.model.Item
import com.team38.wearhacks.internal.model.JsonProtocol._
import spray.json._
import spray.http.MediaTypes._
import spray.http._

  
trait Message {}

trait DBRequest extends Message
case class GetItem(id: String) extends DBRequest
case class AddItem(item: Item) extends DBRequest

trait UpdateServerResponse extends Message {
  def marshal(): HttpResponse
}

trait UpdateServerResponseSuccess extends UpdateServerResponse

case class ItemInfo(item: Item) extends UpdateServerResponseSuccess {
  def marshal() = HttpResponse(StatusCodes.OK, HttpEntity(`application/json`, item.toJson.toString()))
}
case class ItemCreated(item: Item) extends UpdateServerResponseSuccess {
  def marshal() = HttpResponse(StatusCodes.Created, HttpEntity(`application/json`, item.toJson.toString()))
}