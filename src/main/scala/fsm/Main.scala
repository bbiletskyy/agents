package fsm

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging.DebugLevel
import akka.util.Timeout

import scala.concurrent.duration._

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("AgentsSystem")
  system.eventStream.setLogLevel(DebugLevel)
  implicit val timeout = Timeout(1 second)
  val destination = system.actorOf(Props[Target])

  destination ! "Hello"
  Thread.sleep(300)
  destination ! "Hello"
  Thread.sleep(300)
  destination ! "Hello"
  Thread.sleep(300)

  system.terminate()
}


class Target extends Actor with ActorLogging {
  override def receive: Receive = {
    case m => log.info("Message reseived: {}", m)
  }
}
