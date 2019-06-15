package agents.agents2

import java.util.UUID

import agents.Agents.AddAgent
import agents.agents2.ControlProtocol._
import agents.agents2.SystemConfig.{Command, NextStep, UpsertAgents}
import agents.agents2.TransformationProtocol.{Transform, TransformationMsg, Transformed}


trait ControlProtocol extends ProtocolActor with AgentSystem {

  override def preStart(): Unit = {
    super.preStart()
    log.debug("Started AgentsManagementProtocol")
  }

  registerRecieve {
    case Start                => onStart()
    case Stop                 => updateRunning(false)
    case m: Step              => onStep(m)
    case m: Transformed       => onTransformed()
    case m : AddAgents        => onAddAgents(m)

  }

  def onAddAgents(addAgents: AddAgents): Unit = {
    log.debug(s"Adding agents: $addAgents")
    val newAgents = addAgents.agentConfigs.map(cfg => (Agent(cfg), cfg))
    //FIXME
    //upsertAgents(newAgents: _*)
    nextStep()
    self ! Transform(UpsertAgents(addAgents.agentConfigs:_*))
  }

  def onStart(): Unit = {
    updateRunning(true)
    self ! Step()
  }

  def onStep(msg: Step) : Unit = {
    log.info(s"Step: $step")
    nextStep()
    self ! Transform(NextStep)
    sender ! Stepped(msg)
  }

  def onTransformed(): Unit = {
    def drawConfig(acs: List[AgentConfig]): String = {

      val agentPoss = acs.map(_.pos.x.toInt).distinct.sorted
      agentPoss.foldLeft(new StringBuilder("----------")) { (buff: StringBuilder, idx: Int) =>


        buff.setCharAt(idx, '*')
        buff
      }.mkString("[", "", "]")
    }

    log.info(s"Transformed: ${drawConfig(agents.values.toList)}")
    //Thread.sleep(100)
    if(isRunning)
      self ! Step()
  }
}


case class ControlConfig(tick: Long, isRunning: Boolean)

object ControlProtocol {
  sealed trait Msg {
    def id: UUID
  }
  sealed trait Request extends Msg
  sealed trait Response[T<:Request] extends Msg {
    def request: T
    def id: UUID = request.id
  }

  case class Start(id: UUID) extends Request
  object Start {
    def apply(): Start = Start(UUID.randomUUID())
  }
  case class Started(request: Start) extends Response[Start]
  case class Stop(id: UUID) extends Request
  case class Stopped(request: Stop) extends Response[Stop]

  case class Step(id: UUID) extends Request
  object Step {
    def apply(): Step = Step(UUID.randomUUID())
  }
  case class Stepped(request: Step) extends Response[Step]

  case class AddAgents(id: UUID, agentConfigs: List[AgentConfig]) extends Request
  object AddAgents {
    def apply(agentConfigs: AgentConfig*): AddAgents = AddAgents(UUID.randomUUID(), agentConfigs.toList)
  }
  case class AgentsAdded(agentConfig: List[AgentConfig], request: AddAgents) extends Response[AddAgents]
}
