package agents4

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

class Agents extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ => log.info("Hello")
  }
}

object Agents {
  def apply()(implicit system: ActorSystem): ActorRef = system.actorOf(Props[Agents], name = "Agents")
}
