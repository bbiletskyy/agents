package agents.ui
import akka.actor.{Actor, ActorRef}
import agents.Agents.{AddListener, AgentsUpdated, Start, Stop}

class UIActor(agents: ActorRef) extends Actor with ActorAgentManagement {

  val ui = new UIFrame(this)

  override def preStart(): Unit = {
    println("UI starting")
    agents ! AddListener()
  }

  def receive: Receive = {
    case AgentsUpdated(states) =>
      ui.updateAgents(states)
    case _ => println("what?")
  }

  override def getAgents(): ActorRef = agents
}

trait ActorAgentManagement extends AgentManagement {
  def getAgents(): ActorRef

  override def stop(): Unit = getAgents() ! Stop

  override def refresh(): Unit = {
    getAgents() ! Start
  }

  override def start(): Unit = {
    getAgents() ! Start
  }
}
