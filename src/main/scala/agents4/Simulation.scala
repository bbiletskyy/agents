package agents4

import java.util.UUID

import agents3.AgentSystem
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

class Simulation extends Actor with ActorLogging {
  implicit val system: ActorSystem = context.system
  val agents: ActorRef = Agents()

  override def receive: Receive = {
    case _ => log.info("Simmulation")
  }
}

object Simulation {
  def apply()(implicit system: ActorSystem): ActorRef = system.actorOf(Props[AgentSystem], name = "Simulation")

  trait Msg
  trait Request extends Msg
  trait Reply[R<:Request] extends Msg {
    def request: R
  }

  trait Success[R<:Request] extends Reply[R]
  trait Failure[R<:Request] extends Reply[R] {
    def error: String
  }

  case class AddAgents() extends Request
  case class AgentsAdded(request: AddAgents) extends Reply[AddAgents]


  case class AgentCfg(k: Kind, version: Long)

  sealed trait Kind
  case object Matter

}
