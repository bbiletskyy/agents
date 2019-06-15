package agents.agents2

import agents.agents2.ControlProtocol.{AddAgents, Start, Step, Stop}
import agents.agents2.SystemConfig.UpsertAgents
import akka.actor.ActorSystem
import akka.event.Logging._


object Main extends App {
//  def foo(args: String*): Unit = {
//    println(args.size)
//  }
//
//  foo ("3", "4", "5")
  implicit val system = ActorSystem("AgentsSystem")
  system.eventStream.setLogLevel(DebugLevel)
  val agentsSystem = Simmulation()
  Thread.sleep(200)
  //agentsSystem ! AddAgents(AgentConfig(Mass, Pos(4)))
  //Thread.sleep(200)
  agentsSystem ! Start()
//  Thread.sleep(200)
//  agentsSystem ! Step()
//  Thread.sleep(200)

}

