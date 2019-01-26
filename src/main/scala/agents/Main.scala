package agents

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import agents.Agents.{AddAgent, Start}
import agents.ui.{UIActor, UIFrame}

import scala.util.Random


object Main extends App {
  val system = ActorSystem("AgentsSystem")
  val agents = system.actorOf(Props[Agents], name = "Agents")

  for (i <- 1 to 10)
    agents ! AddAgent(State("a", Location(100, 100)))
  val ui = system.actorOf(Props(new UIActor(agents)), name = "UI")
}