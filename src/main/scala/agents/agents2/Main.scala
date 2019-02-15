package agents.agents2

import agents.agents2.ControlProtocol.{Start, Step, Stop}
import akka.actor.ActorSystem
import akka.event.Logging._


object Main extends App {
  implicit val system = ActorSystem("AgentsSystem")
  system.eventStream.setLogLevel(DebugLevel)
  val agentsSystem = AgentSystem()
  agentsSystem ! Start


}

