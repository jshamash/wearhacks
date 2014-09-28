package com.team38.wearhacks.outbound

import akka.actor.ActorRef
import com.team38.wearhacks.core.UserManagerActor
import com.team38.wearhacks.internal.messaging.UpdateServerException
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import spray.http.StatusCodes

trait OutboundConnector extends UserManagerActor {}
trait OutboundDB extends OutboundConnector {
  def exceptionHandler(graph: OrientGraph, sender: ActorRef): PartialFunction[Throwable, Any] = {
    case e: UpdateServerException =>
      graph.rollback()
      sender ! e
    case e: ORecordDuplicatedException =>
      graph.rollback()
      sender ! UpdateServerException(StatusCodes.Conflict,
        "Duplicate record exists",
        e.getMessage())
    case e: Exception =>
      graph.rollback()
      sender ! UpdateServerException(e)
  }
}