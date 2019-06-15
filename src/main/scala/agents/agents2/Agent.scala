package agents.agents2

import java.util.UUID

import agents.agents2.Agent.{Update, Updated}
import agents.agents2.AgentConfig.Move
import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSystem, Props}

import scala.util.Random

class Agent(agentConfig: AgentConfig) extends Actor with ActorLogging {
  private var _config: AgentConfig = agentConfig.copy()
  def config: AgentConfig = _config.copy()
  def updateConfig(config: AgentConfig): Unit = {
    _config = config.copy()
  }

  override def receive: Receive = {
    case m: Update => onUpdate(m)
  }
  def onUpdate(update: Update): Unit = {
    val step = if(Random.nextBoolean()) Pos(-1) else Pos(1)
    val next = agentConfig.next(Move(step))

    updateConfig(next)
    sender ! Updated(config, update)
  }
}


object Agent {
  def apply(agentConfig: AgentConfig)(implicit context: ActorContext): ActorRef = context.actorOf(Props(new Agent(agentConfig)))

  sealed trait AgentMsg {
    def id: UUID
  }

  sealed trait Request extends AgentMsg
  sealed trait Reply[T <: Request] extends AgentMsg {
    def request: T
    def id: UUID = request.id
  }


  case class Update(id: UUID) extends Request
  case class Updated(agentConfig: AgentConfig, request: Update) extends AgentMsg with Reply[Update]
  object Updated {
    def apply(agentCfg: AgentConfig, request: Update): Updated = Updated(agentCfg, request)
  }
}