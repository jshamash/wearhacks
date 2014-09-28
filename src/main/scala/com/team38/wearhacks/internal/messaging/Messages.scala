package com.team38.wearhacks.internal.messaging

import com.team38.wearhacks.internal.model.{Card, Item}
import com.team38.wearhacks.internal.model.JsonProtocol._
import spray.json._
import spray.http.MediaTypes._
import spray.http._

  
trait Message {}

trait DBRequest extends Message
case class GetItem(id: String) extends DBRequest
case class AddItem(item: Item) extends DBRequest
case class AddCard(itemid: String, card: Card) extends DBRequest
case class DeleteItem(id: String) extends DBRequest
case class GetItems() extends DBRequest

trait UpdateServerResponse extends Message {
  def marshal(): HttpResponse
}

trait UpdateServerResponseSuccess extends UpdateServerResponse

case class ItemInfo(item: Item) extends UpdateServerResponseSuccess {
  def marshal() = HttpResponse(StatusCodes.OK, HttpEntity(`application/json`, item.toJson.toString()))
}
case class ItemsInfo(items: List[Item]) extends UpdateServerResponseSuccess {
  def marshal() = HttpResponse(StatusCodes.OK, HttpEntity(`application/json`, items.toJson.toString()))
}
case class ItemCreated(item: Item) extends UpdateServerResponseSuccess {
  def marshal() = HttpResponse(StatusCodes.Created, HttpEntity(`application/json`, item.toJson.toString()))
}
case class ItemUpdated(item: Item) extends UpdateServerResponseSuccess {
  def marshal() = HttpResponse(StatusCodes.OK, HttpEntity(`application/json`, item.toJson.toString()))
}
case class ItemDeleted(item: Option[Item]) extends UpdateServerResponseSuccess {
  def marshal() = HttpResponse(StatusCodes.OK, HttpEntity(`application/json`, item.toJson.toString()))
}