package agents3

import java.util.UUID

import agents3.Agent.{AgentCfg, Update}
import agents3.AgentSystem.{AgentsMsg, Pos}
import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}

class Agent(agentCfg: AgentCfg) extends Actor with ActorLogging {
  var _cfg: AgentCfg = agentCfg
  var _version: Long = 0
  def update(cfg: AgentCfg): Unit = {
    _cfg = cfg
    _version += 1
  }

  override def receive: Receive = {
    case m: Update => _cfg = m.agentCfg
    case m: AgentsMsg => log.error(s"Unknown AgentMsg $m")
    case m => log.error(s"Unknown msg $m")
  }
}

object Agent {

  def apply(agentCfg: AgentCfg)(implicit context: ActorContext): ActorRef = context.actorOf(Props(new Agent(agentCfg)))

  case class AgentCfg(kind: Kind, pos: Pos, id: UUID)
  object AgentCfg {
    def apply(kind: Kind, pos: Pos): AgentCfg = AgentCfg(kind, pos, UUID.randomUUID())
  }

  sealed trait Kind
  case object Matter extends Kind
  case object Charge extends Kind

  sealed trait AgentMsg
  case class Update(agentCfg: AgentCfg) extends AgentMsg
  case class Updated(version: Long, agentCfg: AgentCfg)

}
