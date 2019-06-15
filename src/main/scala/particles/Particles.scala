package particles

import java.util.UUID

import scala.util.Random


trait Matter {
  def kind: Char
  def pos: Int
  def radius: Int
  def id: String
  def ver: Long
  // Distance function TODO: Move to the Position parameter-type of this
  def dist(other: Matter) = Math.abs(this.pos - other.pos)
}

trait Action extends Matter {
  def transform(neighborhood: Map[String, Particle]): Map[String, Particle]
}

trait Particle extends Matter with Action {
  def energy(neighborhood: Map[String, Particle]): Double
}

case class StaticParticle(kind: Char, pos: Int, id: String, ver: Long) extends Particle {
  def radius: Int = 0
  def transform(neighborhood: Map[String, Particle]): Map[String, Particle] = neighborhood
  def energy(neighborhood: Map[String, Particle]) : Double = 0.0
}

case object StaticParticle {
  def apply(kind: Char, pos: Int): StaticParticle = StaticParticle(kind, pos, UUID.randomUUID().toString, 1L)
}

case class FloatingParticle(kind: Char, pos: Int, id: String, ver: Long) extends Particle {
  def radius: Int = 0
  def transform(neighborhood: Map[String, Particle]): Map[String, Particle] = {
    val deltaPos = Random.nextInt(3) - 1
    val newParticle = copy(ver = this.ver + 1, pos = this.pos + deltaPos)
    neighborhood + (newParticle.id -> newParticle)
  }

  def energy(neighborhood: Map[String, Particle]) : Double = {
    val energy: Double = 1.0
    neighborhood.get(this.id).map{ self =>
      val count = neighborhood.filter{case (id, other) => self.dist(other) <= radius}.size
      count * energy
    }.getOrElse(0.0)
  }
}

case object FloatingParticle {
  def apply(kind: Char, pos: Int): FloatingParticle = FloatingParticle(kind, pos, UUID.randomUUID().toString, 1L)
}
