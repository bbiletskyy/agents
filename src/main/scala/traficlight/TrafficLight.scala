package traficlight
import TrafficLight._
import akka.actor.FSM

import scala.concurrent.duration._


/**
  * Traffic light that switches to Red when a pedestrian requests a stop.
  */
object TrafficLight {
  //received events
  case object GetState
  case object RequestStop
  //sent events
  case class GetStateReply(state: State)

  //state
  sealed trait State
  final case object Green extends State
  final case object Yellow extends State
  final case object Red extends State

  //data
  sealed trait Data
  final case object GreenRequested extends Data
  final case object RedRequested extends Data
}

class TrafficLight extends FSM[State, Data] {
  startWith(Green, GreenRequested)

  when(Green) {
    case Event(RequestStop, GreenRequested) =>
      log.info("StopRequested received by traffic light")
      goto(Yellow) using RedRequested
  }

  when(Yellow, stateTimeout = 1 second) {
    case Event(RequestStop, GreenRequested) =>
      log.info("StopRequested received in Yellow x GreenRequested")
      goto(Yellow) using RedRequested
    case Event(StateTimeout, RedRequested) =>

      goto(Red) using RedRequested
    case Event(StateTimeout, GreenRequested) => goto(Green) using GreenRequested
  }

  when(Red, stateTimeout = 2 second) {
    case Event(StateTimeout, RedRequested) =>
      goto(Yellow) using GreenRequested
    case Event(RequestStop, RedRequested) =>
      log.info("StopRequested received in Red x RedRequested")
      stay using RedRequested
//    case Event(RequestStop, GreenRequested) =>
//      log.info(s"StopRequested received in Red x GreenRequested")
//      stay using RedRequested
  }


  whenUnhandled {
    case Event(GetState, _) =>
      log.info("GetState received")
      sender ! GetStateReply(stateName)
      stay

    case Event(e, s) =>
      log.info("Unhandled evnet: {} at state: {}", e, s)
      stay
  }

  initialize()
}
