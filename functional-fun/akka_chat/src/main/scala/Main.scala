import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

class ChatActor extends Actor {
  var messages: Seq[String] = Seq.empty

  override def receive = {
    case Message(_, msg) =>
      messages :+= msg
      sender ! ()

    case GetMessages(_) =>
      sender ! Messages(messages)
  }
}

class ChatSupervisor extends Actor {
  override def receive = {
    case msg: Request =>
      val child = childFor(msg.channel)
      child.forward(msg)
  }

  def childFor(name: String): ActorRef = {
    context.child(name).getOrElse(context.actorOf(Props[ChatActor], name))
  }
}

trait Request {
  def channel: String
}
case class Message(channel: String, msg: String) extends Request
case class GetMessages(channel: String) extends Request
case class Messages(content: Seq[String])

object Main extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val sup = system.actorOf(Props[ChatSupervisor])
  implicit val timeout = Timeout(1.minute)

  val route = pathPrefix("room") {
    path(Segment) { room =>
      post {
        entity(as[String]) { message =>
          onSuccess(sup ? Message(room, message)) { resp =>
            complete(StatusCodes.NoContent)
          }
        }
      } ~
      get {
        onSuccess(sup ? GetMessages(room)) {
          case Messages(resp) =>
            complete(resp.mkString("\n"))
        }
      }
    }
  }

   Http().bindAndHandle(route, "localhost", 8100)
}
