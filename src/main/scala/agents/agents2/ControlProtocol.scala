package agents.agents2

import java.util.UUID

import agents.agents2.ControlProtocol.{AddAgent, Start, Step, Stop}
import agents.agents2.SystemConfig.NextStep
import agents.agents2.TransformationProtocol.{Transform, Transformation}
import akka.actor.ActorRef

trait ControlProtocol extends ProtocolActor with HasSystemConfig {
  override def preStart(): Unit = {
    super.preStart()
    log.debug("Started AgentsManagementProtocol")
  }

  registerRecieve {
    case Start => setSystemConfig(systemConfig.start())
      self ! Transform(NextStep)
    case Stop => setSystemConfig(systemConfig.stop())
    case Step => self ! Transform(NextStep)
    case AddAgent(agentConfig) =>
      val newAgent = Agent(agentConfig)
      self ! Transform
  }
}

object ControlProtocol {
  sealed trait Msg
  case object Start extends Msg
  case object Stop extends Msg
  case object Step extends Msg
  case object Tick extends Msg

  case class AddAgent(agentConfig: AgentConfig) extends Msg
  case class AgentAdded(actor: ActorRef) extends Msg

}
