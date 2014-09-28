package com.team38.wearhacks.outbound

import akka.actor.actorRef2Scala
import com.team38.wearhacks.internal.model.{Item, Image}
import com.tinkerpop.gremlin.scala.ScalaVertex
import com.tinkerpop.gremlin.scala.ScalaVertex._
import spray.http.StatusCodes
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import com.team38.wearhacks.internal.messaging._
import com.team38.wearhacks.tools.Tools
import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.core.metadata.schema.{OClass, OType}
import com.tinkerpop.blueprints.impls.orient.{OrientVertex, OrientGraph, OrientGraphNoTx}

class OrientDBConnector extends OutboundDB {

  val address = config.getString("orientdb.address")
  val username = config.getString("orientdb.user")
  val password = config.getString("orientdb.password")
  val remoteUser = config.getString("orientdb.remote.user")
  val remotePass = config.getString("orientdb.remote.password")

  def vertexToImage(v: ScalaVertex) = Image(v.url.toString, v.time.toString.toInt)
  def vertexToItem(v: ScalaVertex) = {
    val images = v.out("HasImage").toList().map(vertexToImage(_))
    Item(v.itemid.toString, v.name.toString, images, v.audio.toString)
  }

  def receive = {

    // For testing purposes: create the database instance
    case "init1" =>
      log.info("creating database")
      val server = new OServerAdmin(address)
      try {
	      server.connect(remoteUser, remotePass)
	      server.createDatabase("graph", "plocal")
	      sender ! "ok"
	  } catch {
	  	case e: Exception => sender ! UpdateServerException(e)
	  } finally {
	  	server.close
	  }
	  
    // For testing purposes: create classes and indices.
    case "init2" =>
      val graph = new OrientGraphNoTx(address, username, password)
      
      try {
	      log.info("Creating Item class")
	      val itemV = graph.createVertexType("Item")
	      itemV.createProperty("itemid", OType.STRING).setMandatory(true)
	      itemV.createProperty("name", OType.STRING).setMandatory(true)
	      itemV.createProperty("audio", OType.STRING).setMandatory(true)
	      itemV.createIndex("itemIdx", OClass.INDEX_TYPE.UNIQUE, "itemid")
	      
	      log.info("Creating Image class")
	      val imageV = graph.createVertexType("Image")
	      imageV.createProperty("url", OType.STRING).setMandatory(true)
	      imageV.createProperty("time", OType.INTEGER).setMandatory(true)
	      
	      log.info("Creating HasImage edge class")
	      graph.createEdgeType("HasImage")
	      
	      sender ! "ok"
      } catch {
      	case e: Exception => sender ! UpdateServerException(e)
      } finally {
      	graph.shutdown()
      }

    case GetItem(id) =>
      def vertexToImage(v: ScalaVertex) = Image(v.url.toString, v.time.toString.toInt)
      val graph: OrientGraph = new OrientGraph(address, username, password)

      try {
        val itemV: ScalaVertex = graph.getVertices("Item.itemid", id).headOption.getOrElse {
          throw UpdateServerException(StatusCodes.NotFound,
            "Item not found",
            s"No corresponding item with id $id")
        }
        val item = vertexToItem(itemV)
        graph.commit()
        sender ! ItemInfo(item)
      }
      catch exceptionHandler(graph, sender)
      finally graph.shutdown()

    case AddItem(item) =>
      val graph: OrientGraph = new OrientGraph(address, username, password)
      try {
        val imageProps = item.images map Tools.toMap
        val itemProps = Tools.toMap(item) - "images"
        val itemV: OrientVertex = graph.addVertex("class:Item", itemProps.asJava)
        imageProps map { props =>
          val imageV: OrientVertex = graph.addVertex("class:Image", props.asJava)
          itemV.addEdge("HasImage", imageV)
        }
        graph.commit()
        sender ! ItemCreated(item)
      }
      catch exceptionHandler(graph, sender)
      finally graph.shutdown()

    case GetItems() =>
      val graph: OrientGraph = new OrientGraph(address, username, password)

      try {
        val items = graph.getVerticesOfClass("Item").map { v =>
          vertexToItem(v)
        }
        graph.commit()
        sender ! ItemsInfo(items.toList)
      }
      catch exceptionHandler(graph, sender)
      finally graph.shutdown()

    case DeleteItem(id) =>
      val graph: OrientGraph = new OrientGraph(address, username, password)

      try {
        val item = graph.getVertices("Item.itemid", id).map{ vertex =>
          vertex.remove()
          vertexToItem(vertex)
        }.headOption
        graph.commit()
        sender ! ItemDeleted(item)
      }
      catch exceptionHandler(graph, sender)
      finally graph.shutdown()

    case AddImage(image) =>


    case unknown => log.warning("Got unknown message: " + unknown)
  }

}