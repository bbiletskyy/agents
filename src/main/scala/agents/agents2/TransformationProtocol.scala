package agents.agents2

import java.util.UUID

import agents.agents2.Agent.{Update, Updated}
import agents.agents2.ControlProtocol.AddAgents
import agents.agents2.SystemConfig.{Command, NextStep}
import agents.agents2.TransformationProtocol._


import scala.util.Random

trait TransformationProtocol extends ProtocolActor with AgentSystem {

  override def preStart(): Unit = {
    super.preStart()
    log.debug("TransformationProtocol starting")
  }

  registerRecieve {
      case m @ Transform(NextStep, _)                                     => onNextStep(m)
      case m @ Transform(SystemConfig.UpsertAgents(agentConfigs@_*), _)   => onNextStep(m)
  }

  def onNextStep(transform: Transform): Unit = {
    log.debug(s"on Transform: $transform")
    if (agents.isEmpty && transform.command != AddAgents) {
      Transformed(transform)
    } else {
      log.debug(s"on Transform: adents are empty")
      val agent = agents.keys.toList(Random.nextInt(agents.size))
      context.become(transforming(transform), discardOld = false)
      agent ! Update(transform.id)
    }

  }

  private def transforming(transform: Transform): Receive = {
    case m @ Updated(agentConfig: AgentConfig, request: Update) =>
      assert(m.id == transform.id)
      log.debug(s"Updated received, agent: ${m.agentConfig}")
      upsertAgents(sender -> agentConfig)
      unstashAll()
      context.unbecome()
      self ! Transformed(transform)
    case _ => stash()
  }
}

object TransformationProtocol {

  sealed trait TransformationMsg {
    def id: UUID
  }

  trait Reply[T <: TransformationMsg] extends TransformationMsg {
    def request: T
    def id: UUID = request.id
  }
  case class Transform(command: Command, id: UUID) extends TransformationMsg
  object Transform {
    def apply(command: Command): Transform = Transform(command, UUID.randomUUID())
  }
  case class Transformed(request: Transform) extends Reply[Transform]
  object Transformed {
    def apply(transform: Transform): Transformed = Transformed(transform)
  }


}
