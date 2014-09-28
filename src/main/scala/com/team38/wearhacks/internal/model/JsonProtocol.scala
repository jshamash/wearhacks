package com.team38.wearhacks.internal.model

import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport

object JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val imageJson = jsonFormat2(Image)
  implicit val itemJson = jsonFormat(Item, "id", "name", "images", "audio")
}