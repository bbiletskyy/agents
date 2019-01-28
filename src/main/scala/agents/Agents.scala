package agents

import akka.actor.{Actor, ActorRef, Props}
import agents.Agent.{Transform, Transformed}
import agents.Agents._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.util.Random
import akka.actor._
import akka.event._
import com.sun.org.apache.xpath.internal.functions.FuncFalse

class Agents extends Actor {
  import context._
  private val agents: mutable.Map[ActorRef, State] = mutable.Map[ActorRef, State]()
  //private val listeners: ArrayBuffer[ActorRef] = ArrayBuffer()
  private var tickCount: Long = 0
  private def incrementTickCount(count: Long): Unit = tickCount += count
  private val field = new Field()
  private var running: Boolean = false




  def receive = {
    case Tick => tick()
    case Start => onStart()
    case Stop => onPause()
    case Step => onStep()
    case GetState => onGetState(sender())

    case AddAgent(s) => addAgent(s)
    case Transformed(oldState, newState) => updated(sender, oldState, newState)
    case _       => println("huh?")
  }

  override def preStart(): Unit = {

  }

  def addAgent(state:State): Unit = {
    val agent = context.actorOf(Props(new Agent(state, field)))
    agents += (agent -> state)
  }

  def updated(agent: ActorRef, oldState: State, newState: State): Unit = {
    println(s"Tick #$tickCount, $oldState -> $newState")
    agents.put(agent, newState)
    if (running)
      schedule(Tick)
  }

  def onGetState(sender: ActorRef): Unit = {
    sender ! GetStateReply(agents.map{case (_, state) => state}.toList)
  }

  def onStart(): Unit = {
    if(running)
      return
    running = true
    tick()
  }

  def onPause(): Unit = {
    running = false
  }

  def tick(): Unit = {
    incrementTickCount(1)
    if (agents.isEmpty) return
    val keys = agents.keySet.toList
    val size = keys.size
    val agent = keys(Random.nextInt(size))
    agent ! Transform
    println("Agents--[Transform]-->Agent")
  }

  def onStep(): Unit = {
    tick()
  }

  def schedule(msg: Any) = {
    Thread.sleep(1)
    self ! msg
  }
}

object Agents {

  trait AgentsManagement
  case object Start extends AgentsManagement
  case object Step extends AgentsManagement
  case object Stop extends AgentsManagement

  trait AgentsMonitoring
  case object GetState
  case class GetStateReply(states: List[State])

  case object Tick
  case class AddAgent(s: State) extends AgentsManagement

}