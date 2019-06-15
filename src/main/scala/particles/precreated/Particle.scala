package particles.precreated

import akka.actor.{Actor, ActorLogging, ActorRef}
import Particle._

object Particle {
  //received messages
  case class DoTransform(neighborhood: Map[ActorRef, Model])
  //send messages
  case class TransformDone(model: Model)

  // data
  case class Model(version: Long, kind: String, position: Integer) {
    def radius: Integer = 2
  }
}

class Particle(m: Model) extends Actor with ActorLogging {
  private var _model = m
  def model = _model.copy()
  def model_(model: Model) = {_model = model}

  override def preStart(): Unit = {
    log.info(s"Particle started: $model")
  }

  override def receive: Receive = {
    case DoTransform(neighborhood) =>
      log.info(s"Particle DoTransformation($neighborhood) received")
      model_(model.copy(version = model.version))
      sender ! TransformDone(model)
  }
}
