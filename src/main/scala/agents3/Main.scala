package agents3

import java.util.UUID

import agents.agents2.ControlProtocol.AgentsAdded
import agents.agents2.Main.system
import agents3.Agent.{AgentCfg, Matter}
import agents3.AgentSystem.{Pos, Transform, TransformResult, Transformed}
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging.DebugLevel
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("AgentsSystem")
  system.eventStream.setLogLevel(DebugLevel)
  implicit val timeout = Timeout(5 seconds)
  val agents: ActorRef = AgentSystem()



  val transformed = Await.result(agents ? Transform.next, timeout.duration).asInstanceOf[TransformResult]
  println(s"Step: $transformed")

  val transformed1 = Await.result(agents ? Transform.addAgents(AgentCfg(Matter, Pos(1))), timeout.duration).asInstanceOf[TransformResult]
  println(s"Step1: $transformed1")



//  val resultAddAgents = Await.result(agents ? AddAgents(AgentCfg(Matter, Pos(1))), timeout.duration).asInstanceOf[AgentsAdded]
//  println(s"Step: $resultAddAgents")

}
