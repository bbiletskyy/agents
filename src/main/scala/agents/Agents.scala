package agents

import akka.actor.{Actor, ActorRef, Props}
import agents.Agent.{Update, Updated}
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
  private val listeners: ArrayBuffer[ActorRef] = ArrayBuffer()
  private var tickCount: Long = 0
  private def incrementTickCount(count: Long): Unit = tickCount += count
  private val field = new Field()
  private var running: Boolean = false



  def receive = {
    case Start => start()
    case Stop => pause()
    case Tick => tick()
    case AddAgent(s) => addAgent(s)
    case AddListener() => addListener(sender)
    case RemoveListener() => removeListener(sender)
    case Updated(oldState, newState) => updated(sender, oldState, newState)
    case _       => println("huh?")
  }

  override def preStart(): Unit = {
    println("Agents Started")
  }

  def addAgent(state:State): Unit = {
    val agent = context.actorOf(Props(new Agent(state, field)))
    agents += (agent -> state)
  }

  def updated(agent: ActorRef, oldState: State, newState: State): Unit = {
    println(s"Tick #$tickCount, $oldState -> $newState")
    agents.put(agent, newState)
    if (tickCount % 10 == 0)
      listeners.foreach( _ ! AgentsUpdated(agents.map{case (_, state) => state}.toList))
    schedule(Tick)
  }

  def addListener(listener: ActorRef): Unit = listeners += listener
  def removeListener(listener: ActorRef):Unit = listeners -= listener

  def start(): Unit = {
    if(running)
      return
    running = true
    tick()
  }
  def pause(): Unit = {
    running = false
  }

  def tick(): Unit = {
    if (!running) return
    incrementTickCount(1)
    val keys = agents.keySet.toList
    val size = keys.size
    val agent = keys(Random.nextInt(size))
    agent ! Update()
  }

  def schedule(msg: Any) = system.scheduler.scheduleOnce(10 microseconds, self, msg)
}

object Agents {
  case object Start
  case object Stop
  case object Tick
  case class AddAgent(s: State)

  case class AddListener()
  case class RemoveListener()
  case class AgentsUpdated(states: List[State])

}