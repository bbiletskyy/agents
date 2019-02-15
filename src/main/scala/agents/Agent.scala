package agents

import agents.Agent.{FieldChanged, FieldChanging, FieldChangingReply, Transform, Transformed}
import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.math._
import scala.util.Random

class Agent(s: State, field: Field) extends Actor with ActorLogging {
  var _state: State = s
  def state = _state.copy()


  override def preStart(): Unit = {
    field.subscribe(self, s.charge.kind)
  }

  override def receive: Receive = {
    case Transform(step) => transform(sender, step)
    case m                => log.info(s"Huh???  $m")
  }



  def transform(sender: ActorRef, step: Long) = {
    val nextState = s.transform()
    sender ! Transformed(step, s, nextState)
  }

}

object Agent {
  case class Transform(step: Long)
  case class Transformed(step: Long, oldState: State, newState: State)

  case class FieldChanging(agent: ActorRef, prevState: State, nextState: State)
  case class FieldChangingReply(delta: Double)
  case class FieldChanged(agent: ActorRef, prevState: State, nextState: State)
}


