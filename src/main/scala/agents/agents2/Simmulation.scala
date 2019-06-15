package agents.agents2
import scala.collection.mutable
import java.util.UUID

import agents.agents2.AgentConfig.Command
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash}


class Simmulation extends ProtocolActor
  with TransformationProtocol
  with ControlProtocol
  with AgentSystem {

  private val _agents: mutable.Map[ActorRef, AgentConfig] = mutable.Map[ActorRef, AgentConfig]()
  private var _step: Long = 0
  private var _running: Boolean = false
  override def isRunning: Boolean = _running
  override def updateRunning(running: Boolean): Unit = _running = running
  override def step: Long = _step

  override def preStart(): Unit = {
    super.preStart()
    log.debug("Agent System started")
  }
  override def agents: Map[ActorRef, AgentConfig] = _agents.toMap
  override def upsertAgents(upsertedAgents: (ActorRef, AgentConfig)*): Unit = _agents ++= upsertedAgents
  override def nextStep(): Unit = _step += + 1
  override def deleteAgents(agentIds: UUID*): Unit = ???
}

object Simmulation {
  def apply()(implicit system: ActorSystem): ActorRef = system.actorOf(Props[Simmulation], name = "AgentSystem")
}

trait AgentSystem {
  def agents: Map[ActorRef, AgentConfig]
  //def updateAgents(agents: Map[ActorRef, AgentConfig]): Unit
  def upsertAgents(agents: (ActorRef, AgentConfig)*): Unit
  def deleteAgents(agentIds: UUID*): Unit
  def step: Long
  def nextStep(): Unit
  def isRunning: Boolean
  def updateRunning(running: Boolean): Unit
}




sealed trait Kind
case object Mass extends Kind
case object Membrane extends Kind

case class Pos(x: Float)

case class AgentConfig(kind: Kind, pos: Pos, version: Long, id: UUID) {
   def next(command: Command[AgentConfig]): AgentConfig = command.apply(this.copy(version = this.version + 1))
}

object AgentConfig {
  def apply(kind: Kind, pos: Pos): AgentConfig = new AgentConfig(kind, pos, 0, UUID.randomUUID())
  sealed trait Command[C] {
    def apply(c: C): C
  }
  case class Move(delta: Pos) extends Command[AgentConfig] {
    def apply(ac: AgentConfig): AgentConfig = {
//      if (ac.kind == Mass)
//        ac
//      else
        ac.copy(pos = Pos(ac.pos.x + delta.x))
    }
  }
}

case class SystemConfig(tick: Long, running: Boolean, agents: Map[UUID, AgentConfig]) {
  def transform(command: SystemConfig.Command): SystemConfig = command(this)
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
  case class UpsertAgents(agentConfigs: AgentConfig*) extends Command {
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


trait ProtocolActor extends Actor with ActorLogging with Stash {
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
