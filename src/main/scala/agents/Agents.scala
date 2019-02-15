package agents

import agents.Agent.{Transform, Transformed}
import agents.Agents._
import akka.actor._

import scala.collection.mutable
import scala.util.Random

class Agents extends Actor with ActorLogging {
  private val agents: mutable.Map[ActorRef, State] = mutable.Map[ActorRef, State]()
  private var tickCount: Long = 0
  private def incrementTickCount(count: Long): Unit = tickCount += count
  private val field = new Field()
  private var running: Boolean = false


  def receive = {
    case Tick                                     => onTick()
    case Start                                    => onStart()
    case Stop                                     => onPause()
    case Step                                     => onStep()
    case GetState                                 => onGetState(sender())
    case AddAgent(s)                              => onAddAgent(s)
    case Transformed(step, oldState, nextState)   => onTransformed(sender, oldState, nextState)
    case _                                        => println("huh?")
  }

  override def preStart(): Unit = {

  }

  def onAddAgent(state:State): Unit = {
    val agent = context.actorOf(Props(new Agent(state, field)))
    agents += (agent -> state)
  }

  def onTransformed(agent: ActorRef, oldState: State, newState: State): Unit = {
    log.info("===========================")
    log.info(s"Tick #$tickCount, $oldState -> $newState")
    agents.put(agent, newState)
    if (running)
      schedule(Tick)
  }

  def onGetState(sender: ActorRef): Unit = {
    sender ! GetStateReply(tickCount, agents.map{case (_, state) => state}.toList)
  }

  def onStart(): Unit = {
    if(running)
      return

    running = true
    onTick()
  }

  def onPause(): Unit = {
    running = false
  }

  def onTick(): Unit = {
    incrementTickCount(1)
    if (agents.isEmpty) return
    val keys = agents.keySet.toList
    val size = keys.size
    val agent = keys(Random.nextInt(size))
    agent ! Transform(tickCount)
    log.debug(s"Agents--[Transform]-->Agent")
  }

  def onStep(): Unit = {
    onTick()
  }

  def schedule(msg: Any) = {
    //Thread.sleep(10)
    self ! msg
  }
}

object Agents {

  trait AgentsManagement
  case object Start extends AgentsManagement
  case object Step extends AgentsManagement
  case object Stop extends AgentsManagement

  trait AgentsMonitoring
  case object GetState extends AgentsMonitoring
  case class GetStateReply(tick: Long, states: List[State]) extends AgentsMonitoring

  case object Tick
  case class AddAgent(s: State) extends AgentsManagement

}