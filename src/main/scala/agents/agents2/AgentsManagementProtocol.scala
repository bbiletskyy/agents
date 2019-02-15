package agents.agents2

import java.util.UUID

import agents.agents2.AgentsManagementProtocol.{Start, Step, Stop}
import agents.agents2.TransformationProtocol.Transform

trait AgentsManagementProtocol extends ProtocolActor with Agents {
  override def preStart(): Unit = {
    super.preStart()
    log.debug("Started AgentsManagementProtocol")
  }

  registerRecieve {
    case Start =>
      setConfiguration(configuration.start())
      self ! Step
    case Stop =>
      setConfiguration(configuration.stop())
    case Step =>
      self ! Transform(configuration.tick)
  }
}

object AgentsManagementProtocol {
  trait AgentsManagement
  case object Start extends AgentsManagement
  case object Stop extends AgentsManagement
  case object Step extends AgentsManagement
}
