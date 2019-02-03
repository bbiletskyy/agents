package agents.ui
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}
import agents.Agents._

import scala.concurrent.duration._

class UIActor(agents: ActorRef) extends Actor with ActorLogging with ActorAgentManagement {
  import context._
  val ui = new UIFrame(this)
  val timer: Cancellable = system.scheduler.schedule(0 millis, 50 millisecond, agents, GetState)

  override def preStart(): Unit = {
    log.debug("UI starting")
  }

  override def postStop(): Unit = {
    timer.cancel()
  }

  def receive: Receive = {
    case GetStateReply(tickCount, states) => ui.updateAgents(tickCount, states)
    case m => log.warning(s"What?!? $m")
  }

  override def getAgents(): ActorRef = agents
}

trait ActorAgentManagement extends AgentManagement {
  def getAgents(): ActorRef

  override def stop(): Unit = getAgents() ! Stop

  override def step(): Unit = {
    getAgents() ! Step
  }

  override def start(): Unit = {
    getAgents() ! Start
  }
}
