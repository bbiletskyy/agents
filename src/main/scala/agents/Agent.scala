package agents

import agents.Agent.{FieldChanging, FieldChangingReply, FieldChanged, Transformed, Transform}
import akka.actor.{Actor, ActorRef}

import scala.math._
import scala.util.Random

class Agent(s: State, field: Field) extends Actor {
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
    case x @ FieldChanging(agent, state, newState) if agent != self => onFieldChanging(x)
    case FieldChangingReply(delta)                                  => onFieldChangingReply(delta)
    case FieldChanged(agent, state, newState) if agent != self      => fieldChanged(state, newState)
    case x                                                          => //println(s"what?!? $x")
  }

  def fieldChanged(otherState: State, otherNewState: State) = {
    updateState(state.increaseEnergy(state.deltaFieldEnergy(otherState, otherNewState)))
    updateNextState(nextState.increaseEnergy(nextState.deltaFieldEnergy(otherState, otherNewState)))
  }

  def onFieldChangingReply(delta: Double) = {
    println(s"Field--[FieldChangingReply($delta)]-->Agent")
    updateNextState(nextState.copy(energy = state.energy + delta))
  }

  def onFieldChanging(msg: FieldChanging) ={
    println("Field--[FieldChanging]->Agent")
    //val deltaFieldEnergy = fieldEnergy(msg.newState) - fieldEnergy(msg.state)
    val deltaFieldEnergy = nextState.deltaFieldEnergy(msg.prevState, msg.nextState)
    msg.agent ! FieldChangingReply(deltaFieldEnergy)
    println(s"Agent--[FieldChangingReply($deltaFieldEnergy)]->Agent")
  }

  def transform(sender: ActorRef): Unit = {
    println("Agent<-[Transform]--Agents")
    acceptRejectNewState(sender)
    updateNextState(state.transform())
    field.publish(FieldEvent(state.kind, FieldChanging(self, state, nextState)))
    println("Agent--[FieldEvent[FieldChanging]]-->Field")
  }

  def randomAccept(deltaEnergy: Double) = {
    println(s"deltaEnergy: $deltaEnergy, prob = ${exp(-deltaEnergy)}")
    math.random() < exp(-deltaEnergy)
  }

  def acceptRejectNewState(sender: ActorRef): Unit = {
    val oldState = state
    if (randomAccept(deltaEnergy)) {
      updateState(nextState)
    } else {
      updateState(state.nextVersion())
    }
    sender ! Transformed(oldState, state)
    println("Agent--[Transformed]->Agents")
  }
}

object Agent {
  case object Transform
  case class Transformed(oldState: State, state: State)

  case class FieldChanging(agent: ActorRef, prevState: State, nextState: State)
  case class FieldChangingReply(delta: Double)
  case class FieldChanged(agent: ActorRef, prevState: State, nextState: State)
}


