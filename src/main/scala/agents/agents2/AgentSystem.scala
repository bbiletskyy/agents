package agents.agents2


import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}


class AgentSystem extends ProtocolActor
  with TransformationProtocol
  with AgentsManagementProtocol
  with Agents {

  private var _configuration: Configuration = Configuration()
  def configuration: Configuration = _configuration.copy()
  def setConfiguration(configuratiion: Configuration): Unit = {
    _configuration = configuratiion.copy()
  }

  override def preStart(): Unit = {
    super.preStart()
    log.info("Agent System started")
  }
}

trait Agents {
  def configuration: Configuration
  def setConfiguration(configuration: Configuration): Unit
}

object AgentSystem {
  def apply()(implicit system: ActorSystem): ActorRef = system.actorOf(Props[AgentSystem], name = "AgentSystem")
  trait Msg
  trait External extends Msg
  trait Internal extends Msg
}


case class Configuration(tick: Long, running: Boolean) {
  def transform(): Configuration = if (running) step() else this
  def step(): Configuration = this.copy(tick = this.tick + 1)
  def start(): Configuration = this.copy(running = true)
  def stop(): Configuration = this.copy(running = false)
}

object Configuration {
  def apply(): Configuration = Configuration(tick = 0, running = false)
}


trait ProtocolActor extends Actor with ActorLogging {
  private var receives: List[Receive] = List()
  protected def registerRecieve(receive: Receive): Unit = {
    receives = receive :: receives
  }

  def receive: Receive = receives.reduce(_ orElse _)
}