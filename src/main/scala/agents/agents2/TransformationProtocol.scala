package agents.agents2

import java.util.UUID

import agents.agents2.SystemConfig.{Command, NextStep}
import agents.agents2.TransformationProtocol._
import akka.actor.{Actor, ActorLogging}

trait TransformationProtocol extends ProtocolActor with HasSystemConfig {

  override def preStart(): Unit = {
    super.preStart()
    log.debug("TransformationProtocol starting")
  }

  registerRecieve {
      case m: Transform     => onTransform(m)
      case m: Transformed  => onTransformed()
  }

  def onTransform(m: Transform): Unit = {
    setSystemConfig(m.command(systemConfig))
    log.info(s"Step # ${systemConfig.tick}")
    self ! Transformed(m)
  }

  def onTransformed(): Unit = {
    log.info(s"Step # ${systemConfig.tick} completed: ${systemConfig}")
    if(!systemConfig.running)
      return
    Thread.sleep(200)
    self ! Transform(NextStep)
  }

}

object TransformationProtocol {

  sealed trait Transformation {
    def id: UUID
  }
  case class Transform(id: UUID, command: Command) extends Transformation

  case object Transform {
    def apply(command: Command): Transform = Transform(UUID.randomUUID(), command)

  }
  case class Transformed(id: UUID) extends Transformation
  object Transformed {
    def apply(transform: Transform): Transformed = Transformed(transform.id)
  }


}
