package particles

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}

import scala.collection.{immutable, mutable}
import scala.util.Random
import Config.{Transform, _}

object Config {
  //received mesages
  final case object Transform
  final case class SetModel(m: Model)

  //sent messages
  final case class Transformed(model: Model)
  final case class ModelSet(m: Model)

  //data
  case class Model(version: Long, particles: Set[Particle]) {
    def display() = {
      val posKind = particles.map(p => (p.pos, p.kind)).toMap.withDefaultValue("_")
      val minPos = particles.minBy(p => p.pos).pos
      val maxPos = particles.maxBy(p => p.pos).pos
      val cfg = for (i <- Range.inclusive(minPos, maxPos)) yield posKind(i)
      s"${cfg.mkString}  v. $version"
    }
  }


//  case class Particle(kind: String, pos: Int, id: String, ver: Long) {
//    def radius: Int = 0
//    def energy(): Double = 1.0
//    def dist(other: Particle) = Math.abs(this.pos - other.pos)
//
//    def transform(neighborhood: Map[String, Particle]): Map[String, Particle] = {
//      if(this.kind == "*") {
//        val deltaPos = Random.nextInt(3) - 1
//        val newParticle = copy(ver = this.ver + 1, pos = this.pos + deltaPos)
//        neighborhood + (newParticle.id -> newParticle)
//      } else {
//        neighborhood
//      }
//    }
//
//    def energy(neighborhood: Map[String, Particle]) : Double = {
//      neighborhood.get(this.id).map{ self =>
//        val count = neighborhood.filter{case (id, other) => self.dist(other) <= radius}.size
//        count * energy
//      }.getOrElse(0.0)
//    }
//  }
//  case object Particle {
//    def apply(kind: String, pos: Int): Particle = Particle(kind, pos, UUID.randomUUID().toString, 1L)
//  }

}

class Config extends Actor with ActorLogging {
  private var _version: Long = 0
  def version() = _version
  def version_(version: Long) = {_version = version}

  private val _particles: mutable.Map[String, Particle] = mutable.Map.empty
  def particles(): Set[Particle] = _particles.values.toSet
  def particles_(particles: Set[Particle]): Unit = {
    _particles.clear()
    _particles ++= particles.map(particle => (particle.id -> particle))
  }

  def model() = Model(version, particles)

  override def receive: Receive = {
    case Transform =>
      log.info("Configuration received Transform")

      val randomParticle: Particle = _particles.toList(Random.nextInt(_particles.keys.size))._2
      val neighborhood: Map[String, Particle] = _particles.filter{ case (_, particle) => Math.abs(randomParticle.pos - particle.pos) <= randomParticle.radius}.toMap
      val energy = randomParticle.energy(neighborhood)
      val newNeighborhood: Map[String, Particle] = randomParticle.transform(neighborhood)
      val newEnergy = randomParticle.energy(newNeighborhood)
      val deltaEnergy = newEnergy - energy
      if (deltaEnergy < 0) {
        log.info(s"Transformed: deltaEnergy = $deltaEnergy, energy = $energy, newEnmergy = $newEnergy")
        _particles --= neighborhood.map{case (id, _) => id}
        _particles ++= newNeighborhood
      } else {
        log.info(s"Rejected: deltaEnergy = $deltaEnergy, energy = $energy, newEnmergy = $newEnergy")
      }
      version_(version + 1)
      sender ! Transformed(model())

    case SetModel(m) =>
      log.info(s"Configuration received SetModel $m")
      particles_(m.particles)
      version_(m.version)
      sender ! ModelSet(model())
  }
}



