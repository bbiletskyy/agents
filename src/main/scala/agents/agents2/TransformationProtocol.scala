package agents.agents2

import agents.agents2.TransformationProtocol.{Transform, Transformed}
import akka.actor.{Actor, ActorLogging}

trait TransformationProtocol extends ProtocolActor with Agents {

  override def preStart(): Unit = {
    super.preStart()
    log.debug("TransformationProtocol starting")
    //self ! Transform(configuration.tick)
  }

  registerRecieve {
      case m: Transform     => onTransform(m)
      case m: Transformed   => onTransformed(m)
  }

  def onTransform(m: Transform): Unit = {
    log.info(s"Transformation started, tick: ${m.tick}")
    setConfiguration(configuration.transform())
    self ! Transformed(configuration.tick, m)
  }

  def onTransformed(m: Transformed): Unit = {
    log.info(s"Transformation finished, tick ${m.tick}, config: ${configuration}")
    if(configuration.running)
      self ! Transform(m.tick)
  }

}

object TransformationProtocol {
  sealed trait Transformation {
    def tick: Long
  }
  case class Transform(tick: Long) extends Transformation
  case class Transformed(tick: Long, request: Transform) extends Transformation
}
