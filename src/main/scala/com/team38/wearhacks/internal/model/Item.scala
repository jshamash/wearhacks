package com.team38.wearhacks.internal.model

case class Item(itemid: String, name: String, cards: List[Card], audioUrl: String)
case class Card(imageUrl: String, text: String, time: Int)