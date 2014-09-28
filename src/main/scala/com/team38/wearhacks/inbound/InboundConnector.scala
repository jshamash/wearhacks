package com.team38.wearhacks.inbound

import com.team38.wearhacks.core.UserManagerActor
import com.team38.wearhacks.internal.messaging.{UpdateServerException, UpdateServerResponseSuccess}
import net.jalg.hawkj.{Algorithm, HawkContext}
import spray.http.HttpEntity.apply
import spray.http.HttpHeaders._
import spray.http.{ContentTypes, GenericHttpCredentials, HttpChallenge, HttpCredentials, HttpHeader, HttpRequest, HttpResponse}
import spray.http.StatusCodes.InternalServerError
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.httpx.marshalling.ToResponseMarshaller
import spray.routing.{HttpServiceActor, RejectionHandler, RequestContext}
import spray.routing.authentication.HttpAuthenticator

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait InboundConnector extends HttpServiceActor with UserManagerActor {
  def genericReply(implicit ctx: RequestContext): Try[Any] => (spray.routing.RequestContext => Unit) = {
    case Success(response: UpdateServerResponseSuccess) =>
      ctx => complete(response.marshal)
    case Success(e: UpdateServerException) =>
      log.warning("Responding with " + e)
      ctx => complete(e.marshal)
    case Failure(e: UpdateServerException) =>
      log.warning("Responding with " + e)
      ctx => complete(e.marshal)
    case other =>
      log.warning("Responding with " + other)
      ctx => complete(HttpResponse(InternalServerError).withEntity(other.toString))
  }
  
  implicit def FutureMarshaller(implicit exctx: ExecutionContext) = 
    ToResponseMarshaller.of[Future[Any]](ContentTypes.`application/json`) {(value, contentType, ctx) =>
      value onComplete {
        case Success(response: UpdateServerResponseSuccess) =>
          ctx.marshalTo(response.marshal)
        case Success(e: UpdateServerException) =>
          log.warning("Responding with " + e)
          ctx.marshalTo(e.marshal)
        case Failure(e: UpdateServerException) =>
          log.warning("Responding with " + e)
          ctx.marshalTo(e.marshal)
        case Success(e: Exception) =>
          log.warning("Responding with " + e)
          ctx.marshalTo(UpdateServerException(e).marshal)
        case Failure(e: Exception) =>
          log.warning("Responding with " + e)
          ctx.marshalTo(UpdateServerException(e).marshal)
        case unknown =>
          log.warning("Responding with " + unknown)
          ctx.marshalTo(HttpResponse(InternalServerError).withEntity(unknown.toString))
      }
  }
  
  def jsonifyError(response: HttpResponse): HttpResponse = {
    UpdateServerException(response.status, response.entity.asString, "No additional info").marshal()
  }
  
  implicit val rejectionHandler = RejectionHandler {
    case rejections => mapHttpResponse(jsonifyError) {
      RejectionHandler.Default(rejections)
    }
  }
}


case class Hawk(id: String, mac: String, ts: Long, nonce: String)

class HawkHttpAuthenticator(realm: String)(implicit val executionContext: ExecutionContext)
  extends HttpAuthenticator[String] {
  
  def lookup(id: String): Future[String] = Future{ "password" }
  
  def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext): Future[Option[String]] = {
    println("Authenticating...")    
    
    credentials match {
      case Some(GenericHttpCredentials(scheme, token, params)) if scheme == "Hawk" =>
        val hawk = Try(Hawk(params("id"), params("mac"), params("ts").toLong, params("nonce")))
        hawk match {
          case Success(h) =>
            lookup(h.id).map { pass =>
              // Note: the second param is the full URI, not just the path (which the protocol specifies).
              // This is done in order to be compatible with updateservicectl, which uses this "incorrect" way of calculating the MAC.
              val hawkContext = Try(HawkContext.request(ctx.request.method.toString,
									          ctx.request.uri.toString,
									          ctx.request.uri.authority.host.toString,
									          ctx.request.uri.authority.port)
									         .credentials(h.id, pass, Algorithm.SHA_256)
									         .tsAndNonce(h.ts, h.nonce)
									         .build())
				hawkContext match {					         
	            	case Success(hc) if hc.isValidMac(h.mac) => println("valid mac"); Some(h.id)
	            	case Success(hc) => println("auth failed, hawkcontext is " + hc); println(s"Expected mac = ${hc.createAuthorizationHeader().getMac()}, got ${h.mac}"); None
	            	case Failure(e) => println("auth failed: " + e); None
	          	}
            }
          case _ => println("Couldn't form hawk request"); Future { None }
        }
      case _ => println("Not Hawk authorization"); Future { None }
    }
  }
  
  def getChallengeHeaders(httpRequest: HttpRequest): List[HttpHeader] =
    `WWW-Authenticate`(HttpChallenge(scheme = "Hawk", realm, Map.empty)) :: Nil
}

object HawkAuth {
  def apply()(implicit ec: ExecutionContext): HawkHttpAuthenticator = new HawkHttpAuthenticator("Update Server")
  def apply(realm: String)(implicit ec: ExecutionContext): HawkHttpAuthenticator = new HawkHttpAuthenticator(realm)
}