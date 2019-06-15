package agents3

import java.util.UUID

import agents.Agents.AddAgent
import agents3.Agent.{AgentCfg, Kind, Update}
import agents3.AgentSystem._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash}

import scala.collection.mutable
import scala.collection.immutable.Seq

class AgentSystem extends Actor with ActorLogging with Simulation with Stash with Agents {

  override def receive: Receive = {
    case m: Transform => onTransform(m)
    case m => log.info(s"$m")
  }
  def onTransform(transform: Transform): Unit = {
    addStep()
    log.info(s"Step: ${steps()}, message: $transform")
    val from  = sender()
    context.become(transforming(from), discardOld = false)
    self ! transform.command
    log.info("[----------]")
  }


  def transforming(from: ActorRef): Receive = {
    case Next =>
      log.info(s"Command: $Next")
      from ! Transformed(steps, Transform(Next))
      unstashAll()
      context.unbecome()
    case m: Upsert =>
      log.info(s"Command: $m")

      upsert(m.cfgs:_*)

      from ! Transformed(steps, Transform(m))
      unstashAll()
      context.unbecome()
    case m => log.info(s"Stashing unknown message while transforming: $m")
      stash()
  }

//  def upsert(agentCfg: AgentCfg*): Unit = {
//    val newAgents =
//  }

}

trait Simulation {
  var _paused: Boolean = true
  var _steps: Long = 0
  def addStep(): Unit = _steps += 1
  def steps(): Long = _steps
  def setPaused(paused: Boolean): Unit = _paused = paused
  def paused: Boolean = _paused
}

trait Agents { this: Actor =>
  val _idAgentActorRefs: mutable.Map[UUID, ActorRef] = mutable.Map.empty
  def upsertAgents(idActors: Map[UUID, ActorRef]):Unit = _idAgentActorRefs ++= idActors
  def deleteAgents(ids: UUID*):Unit = _idAgentActorRefs --= ids

  def upsert(agentCfgs: AgentCfg*): Unit = {
    val upsertedAgents = agentCfgs.map(_.id).toSet
    val newAgents = upsertedAgents -- _idAgentActorRefs.keySet
    val existingAgents = upsertedAgents -- newAgents

    newAgents.foreach(cxfg => Agent)


    agentCfgs.foreach { cfg =>
      if (_idAgentActorRefs.contains(cfg.id)) {

      }


      val agentActorRef = _idAgentActorRefs.getOrElse(cfg.id, Agent(cfg))
      _idAgentActorRefs.put(cfg.id, agentActorRef)
      agentActorRef ! Update(cfg)
    }
  }

}


object AgentSystem {
  def apply()(implicit system: ActorSystem): ActorRef = system.actorOf(Props[AgentSystem], name = "AgentSystem")

  case class Pos(x: Int)

  sealed trait AgentsMsg
  case class Transform(command: Command) extends AgentsMsg
  object Transform {
    def next(): Transform = Transform(Next)
    def addAgents(agentCfgs: AgentCfg*):Transform = Transform(Upsert(Seq(agentCfgs:_*)))
  }
  trait TransformResult extends AgentsMsg {
    def request: Transform
  }
  case class Transformed(step: Long, request: Transform) extends TransformResult
  case class TransformFailed(msg: String, request: Transform) extends TransformResult


  sealed trait Command
  case object Next extends Command
  case class Upsert(cfgs: Seq[AgentCfg]) extends Command
  case class Delete(ids: Seq[UUID]) extends Command

}
