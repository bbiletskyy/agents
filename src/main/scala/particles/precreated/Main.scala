package particles.precreated

import akka.actor.{ActorSystem, Props}
import akka.event.Logging.DebugLevel
import akka.util.Timeout
import scala.concurrent.duration._

object Main {
  def main(args: Array[String]): Unit = {

    println("Starting Particles")
    implicit val system: ActorSystem = ActorSystem("PatriclesSystem")
    system.eventStream.setLogLevel(DebugLevel)
    implicit val timeout = Timeout(1 second)
    val config = system.actorOf(Props[Config], "cfg")

    config ! Config.Transform

  }
}
