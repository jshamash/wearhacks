package com.team38.wearhacks.core

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.Properties

import com.team38.wearhacks.inbound.InboundConnector
import com.team38.wearhacks.inbound.RestfulInboundConnector
import com.team38.wearhacks.outbound.OrientDBConnector
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("Wearhacks")
  val config = ConfigFactory.load()

  implicit val timeout = Timeout(config.getInt("service.timeout") seconds)

  val dbConnector = system.actorOf(Props[OrientDBConnector], "db-connector")
  val inboundConnector = system.actorOf(Props(classOf[RestfulInboundConnector], dbConnector), "inbound-connector")

  val iface = config.getString("service.interface")
  val port = Properties.envOrNone("PORT") match {
    case Some(p) => p.toInt
    case None => config.getInt("service.port")
  }

  IO(Http) ? Http.Bind(inboundConnector, interface = iface, port = port)
}
