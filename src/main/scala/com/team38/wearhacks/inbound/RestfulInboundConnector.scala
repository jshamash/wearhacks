package com.team38.wearhacks.inbound

import akka.actor.ActorRef
import akka.pattern.ask
import com.team38.wearhacks.internal.messaging._
import com.team38.wearhacks.internal.model._

class RestfulInboundConnector(db: ActorRef) extends InboundConnector {

  import JsonProtocol._

  val route = {
    path("") {
      get {
        complete {
          <html>
            <body>
              <h1>Welcome to our project!</h1>
            </body>
          </html>
        }
      }
    } ~
    sealRoute {
      path("init") {
        post {
          complete {
            db ? "init2"
          }
//            (db ? "init1").flatMap {
//              case _ =>
//                db ? "init2"
//            }
        }
      } ~
      path("items") {
        get {
          complete { db ? GetItems() }
        } ~
        post {
          entity(as[Item]) { item =>
            complete { db ? AddItem(item) }
          }
        }
      } ~
      path("items" / Segment) { id =>
        get {
          complete { db ? GetItem(id) }
        } ~
        delete {
          complete { db ? DeleteItem(id) }
        }
      } ~
      path("items" / Segment / "images") { id =>
        put {
          entity(as[Image]) { img =>
            complete { db ? AddImage(id, img) }
          }
        }
      } ~
      pathPrefix("images") {
        getFromResourceDirectory("images")
      } ~
      pathPrefix("audio") {
        getFromResourceDirectory("audio")
      }
    }
  }

  def receive = runRoute(route)
}