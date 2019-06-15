package agents4

import akka.actor.ActorSystem
import akka.event.Logging.DebugLevel
import akka.util.Timeout

import scala.concurrent.duration._

object Main extends App {
  println("Hello")
  implicit val system: ActorSystem = ActorSystem("AgentsSystem")
  system.eventStream.setLogLevel(DebugLevel)
  implicit val timeout = Timeout(1 second)
  val simulation = Simulation()
  simulation ! "Hello"

}
