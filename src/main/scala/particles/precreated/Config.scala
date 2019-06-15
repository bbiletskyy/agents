package particles.precreated

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import particles.precreated.Config._

import scala.collection.{immutable, mutable}
import scala.util.Random

object Config {
  //received mesages
  final case object Transform
  final case class SetModel(m: Model)

  //sent messages
  final case class Transformed(model: Model)
  final case class ModelSet(model: Model)


  //data
  case class Model(version: Long, particles: immutable.Seq[Particle.Model]) {
    def display() = {
      val posKind = particles.map(p => (p.position, p.kind)).toMap.withDefaultValue("_")
      val minPos = particles.minBy(p => p.position).position
      val maxPos = particles.maxBy(p => p.position).position
      val cfg = for (i <- Range.inclusive(minPos, maxPos)) yield posKind(i)
      s"${cfg.mkString}  v. $version"
    }
  }

}

class Config extends Actor with ActorLogging {
  private var _version: Long = 0
  def version() = _version
  def version_(version: Long) = {_version = version}
  def incVersion() = version_(version + 1)
  private val _particles: mutable.Map[ActorRef, Particle.Model] = mutable.Map.empty
  def particles(): immutable.Seq[Particle.Model] = _particles.values.toList
  def particles_(particles: immutable.Seq[Particle.Model]) = {
    _particles.keys.foreach(_ ! PoisonPill)
    _particles.clear()
    particles.foreach { particleModel =>
      val newParticle = context.actorOf(Props(new Particle(particleModel)), s"Hello${UUID.randomUUID().toString.take(3).mkString}")
      _particles += (newParticle -> particleModel)
    }
  }
  def model() = Model(version, particles)

  override def receive: Receive = idle

  def idle: Receive = {
    case Transform =>
      log.info("Configuration received Transform")
      val randomIndex = Random.nextInt(_particles.keys.size)
      val particle = _particles.keysIterator.toList(randomIndex)
      val (actor, model) = randomParticle

      log.info(s"Sending DoTransform to $particle")
      actor ! Particle.DoTransform(neighborhood(model))
      val from = sender()
      context.become(transforming(particle, from))

    case SetModel(m) =>
      log.info(s"Configuration received SetModel: $m")
      version_(m.version)
      particles_(m.particles)
      sender ! ModelSet(model)
  }

  def randomParticle: (ActorRef, Particle.Model) =
    _particles.iterator.toList(Random.nextInt(_particles.keys.size))


  def neighborhood(particle: Particle.Model): Map[ActorRef, Particle.Model] =
    _particles.filter{ case (actor, otherParticle) =>
      Math.abs(particle.position - otherParticle.position) <= particle.radius * 2
    }.toMap

  def transforming(particle: ActorRef, trigger: ActorRef): Receive = {
    case Particle.TransformDone(particleModel) =>
      log.info(s"Sender: ${sender()}, particle: $particle")
      assert(sender == particle)
      log.info(s"Config received TransformDone from ${sender}")
      _particles += (particle -> particleModel)
      incVersion()
      trigger ! Transformed(model)
      context.become(idle)
  }
}



