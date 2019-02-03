package agents

import agents.Agent.{FieldChanged, FieldChanging, FieldChangingReply, Transform, Transformed}
import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.math._
import scala.util.Random

class Agent(s: State, field: Field) extends Actor with ActorLogging {
  //private var _dynamicState = DynamicState(s)

  private var _currState: State = s
  private var _nextState: State = s

  private def state: State = _currState.copy()
  private def updateState(s: State): Unit  = this._currState = s
  private def nextState: State = this._nextState.copy()
  private def updateNextState(s: State): Unit  = this._nextState = s
  private def deltaEnergy = nextState.energy - state.energy

  override def preStart(): Unit = {
    field.subscribe(self, s.kind)
  }

  override def receive: Receive = {
    case Transform => transform(sender)
    case FieldChanging(agent, currState, nextState) if agent != self  => onFieldChanging(agent, currState, nextState)
    case FieldChangingReply(delta)                                    => onFieldChangingReply(delta)
    case FieldChanged(agent, state, newState) if agent != self        => fieldChanged(state, newState)
    case x                                                            => //log.debug(s"what?!? $x")
  }

  def fieldChanged(otherState: State, otherNewState: State) = {
    updateState(state.increaseEnergy(state.deltaFieldEnergy(otherState, otherNewState)))
    updateNextState(nextState.increaseEnergy(nextState.deltaFieldEnergy(otherState, otherNewState)))
  }

  def onFieldChangingReply(delta: Double) = {
    log.debug(s"Field--[FieldChangingReply($delta)]->Agent")
    updateNextState(nextState.copy(energy = state.energy + delta))
  }

  def onFieldChanging(agent: ActorRef, prevState: State, nextState: State) ={
    log.debug("Field--[FieldChanging]->Agent")
    val deltaFieldEnergy = nextState.deltaFieldEnergy(prevState, nextState)
    agent ! FieldChangingReply(deltaFieldEnergy)
    log.debug(s"Agent(${state.id})--[FieldChangingReply($deltaFieldEnergy)]->Agent")
  }

  def transform(sender: ActorRef): Unit = {
    log.debug(s"Agent(${state.id})<-[Transform]--Agents")
    acceptRejectNewState(sender)
    updateNextState(state.transform())
    field.publish(FieldEvent(state.kind, FieldChanging(self, state, nextState)))
    log.debug(s"Agent(${state.id})--[FieldEvent[FieldChanging]]->Field")
  }

  def randomAccept(deltaEnergy: Double) = {
    val temperature = 1
    val prob = exp(-deltaEnergy/temperature)
    val r = math.random()
    val accepted = r < prob
    if (accepted)
      log.info(s"Accepted: id=${state.id}, deltaEnergy: $deltaEnergy, prob = $prob, r = $r")
    else
      log.info(s"Rejected: id=${state.id}, deltaEnergy: $deltaEnergy, prob = $prob, r = $r")
    accepted
  }

  def acceptRejectNewState(sender: ActorRef): Unit = {
    val oldState = state
    if (randomAccept(deltaEnergy)) {
      updateState(nextState)
    } else {
      updateState(state.nextVersion())
    }
    sender ! Transformed(oldState, state)
    log.debug("Agent--[Transformed]->Agents")
  }
}

object Agent {
  case object Transform
  case class Transformed(oldState: State, state: State)

  case class FieldChanging(agent: ActorRef, prevState: State, nextState: State)
  case class FieldChangingReply(delta: Double)
  case class FieldChanged(agent: ActorRef, prevState: State, nextState: State)
}


