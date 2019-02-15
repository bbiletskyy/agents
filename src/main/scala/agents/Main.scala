package agents

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import agents.Agents.{AddAgent, Start}
import agents.ui.{UIActor, UIFrame}
import akka.event.Logging._

import scala.collection.mutable
import scala.util.Random


object Main extends App {
  val system = ActorSystem("AgentsSystem")
  val agents = system.actorOf(Props[Agents], name = "Agents")


  system.eventStream.setLogLevel(DebugLevel)
  for (i <- 1 to 1)
    agents ! AddAgent(State(s"a$i", "a", 100 * i, 100*i))
  val ui = system.actorOf(Props(new UIActor(agents)), name = "UI")
}