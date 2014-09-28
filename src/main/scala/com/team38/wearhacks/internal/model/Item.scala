package com.team38.wearhacks.internal.model

case class Item(itemid: String, name: String, images: List[Image], audio: String)
case class Image(url: String, time: Int)