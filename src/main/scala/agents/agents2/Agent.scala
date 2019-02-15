package agents.agents2

import agents.Agent
import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSystem, Props}

class Agent(agentConfig: AgentConfig) extends Actor with ActorLogging {
  private var _config: AgentConfig = agentConfig.copy()

  def configuration: AgentConfig = _config.copy()
  def setConfiguration(configuration: AgentConfig): Unit = {
    _config = configuration.copy()
  }

  override def receive: Receive = {
    case _ => log.info("Something arrived")
  }

}


object Agent {
  //context.actorOf(Props(new Agent(state, field)))
  def apply(agentConfig: AgentConfig)(implicit context: ActorContext): ActorRef = context.actorOf(Props(new Agent(agentConfig)))
}