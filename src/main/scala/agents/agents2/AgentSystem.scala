package agents.agents2


import java.util.UUID

import agents.agents2.SystemConfig.Command
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}


class AgentSystem extends ProtocolActor
  with TransformationProtocol
  with ControlProtocol
  with HasSystemConfig {

  private var _config: SystemConfig = SystemConfig()
  def systemConfig: SystemConfig = _config.copy()
  def setSystemConfig(configuratiion: SystemConfig): Unit = {
    _config = configuratiion.copy()
  }

  override def preStart(): Unit = {
    super.preStart()
    log.info("Agent System started")
  }
}

object AgentSystem {
  def apply()(implicit system: ActorSystem): ActorRef = system.actorOf(Props[AgentSystem], name = "AgentSystem")
}

trait HasSystemConfig {
  def systemConfig: SystemConfig
  def setSystemConfig(systemConfig: SystemConfig): Unit
}


sealed trait Kind
case object Mass extends Kind
case object Charge extends Kind

case class Pos(x: Float)
case class AgentConfig(id: UUID, kind: Kind, pos: Pos, actorRef: ActorRef)
case class SystemConfig(tick: Long, running: Boolean, agents: Map[UUID, AgentConfig]) {

  def transform(command: Command): SystemConfig = command(this)
  def nextTick(): SystemConfig = this.copy(tick = this.tick + 1)
  def start(): SystemConfig = this.copy(running = true)
  def stop(): SystemConfig = this.copy(running = false)
}

object SystemConfig {
  def apply(): SystemConfig = SystemConfig(tick = 0, running = false, agents = Map())

  sealed trait Command {
    def apply(systemConfig: SystemConfig): SystemConfig
  }
  case object Identity extends Command {
    def apply(systemConfig: SystemConfig): SystemConfig = systemConfig.copy()
  }
  case object NextStep extends Command {
    def apply(systemConfig: SystemConfig): SystemConfig = systemConfig.copy(tick = systemConfig.tick + 1)
  }
  case class AddAgents(agentConfigs: Array[AgentConfig]) extends Command {
    def apply(systemConfig: SystemConfig): SystemConfig = {
      systemConfig.copy(
        agents = systemConfig.agents ++ agentConfigs.map(ac => (ac.id -> ac)),
        tick = systemConfig.tick + 1
      )
    }
  }

  case class RemoveAgents(ids: Array[UUID]) extends Command {
    def apply(systemConfig: SystemConfig): SystemConfig = {
      systemConfig.copy(
        tick = systemConfig.tick + 1,
        agents = systemConfig.agents -- ids)
    }
  }

  case class UpdateAgents(agentConfigs: Array[AgentConfig]) extends Command {
    def apply(systemConfig: SystemConfig): SystemConfig = {
      NextStep(systemConfig).copy(
        agents = systemConfig.agents ++ agentConfigs.map(agentConfig => (agentConfig.id, agentConfig))
      )
    }
  }

}


trait ProtocolActor extends Actor with ActorLogging {
  override def preStart(): Unit = {
    super.preStart()
    log.debug("Protocol Actor starting")
  }
  private var receives: List[Receive] = List()
  protected def registerRecieve(receive: Receive): Unit = {
    receives = receive :: receives
  }

  def receive: Receive = receives.reduce(_ orElse _)
}
