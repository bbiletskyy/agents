package agents

import akka.actor.{Actor, ActorRef}
import agents.Agent.{PotentialChange, StateChanging, Update, Updated}

import scala.util.Random

class Agent(s: State, field: Field) extends Actor {
  private var _state: State = s
  //private var deltaEnergy = 0d
  private var _newState: State = s

  private def getState: State = _state.copy()
  private def setState(s: State): Unit  = this._state = s
  private def getNewState: State = this._newState.copy()
  private def setNewState(s: State): Unit  = this._newState = s
  private def deltaEnergy = getNewState.energy - getState.energy

  override def preStart(): Unit = {
    field.subscribe(self, s.kind)
  }

  override def receive: Receive = {
    case x: Update => {
      val s = sender()
      update(s)
    }
    case x @ StateChanging(agent, state, newState) => agent ! PotentialChange(0, x)
    case PotentialChange(delta, stateChanging) => 1+7
    case x => println(s"what?!? $x")
  }


  def acceptState() = {
    setState(getNewState)
  }

  def update(sender: ActorRef): Unit = {
    if (deltaEnergy <= 0)
      acceptState()

    setNewState(transformState(getState))
    field.publish(FieldEvent(getState.kind, StateChanging(self, getState, getNewState)))

    sender ! Updated(getState, getNewState)
  }

  def transformState(state: State): State = {
    val dx = Random.nextGaussian()
    val dy = Random.nextGaussian()

    state.copy(location=Location(state.location.x + dx, state.location.y + dy))
  }
}

object Agent {
  case class Update()
  case class Updated(oldState: State, newState: State)

  trait StateChange
  case class StateChanging(agent: ActorRef, state: State, newState: State) extends StateChange
  case class PotentialChange(delta: Double, stateChanging: StateChanging) extends StateChange
}

