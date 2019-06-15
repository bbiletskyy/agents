package particles.demon

import java.util.UUID

import scala.util.Random


//trait Matter[K, P, I] {
//  def kind: K
//  def pos: P
//  def id: I
//  def radius: I
//}
//
//trait Charge[K, P, I] extends Matter[K, P, I] {
//  def energy(neighborhood: Map[I, Matter[K, P, I]]): Double
//}
//
//trait Action[K, P, I] extends Matter[K, P, I] {
//  def transform(neighbornood: Map[I, Matter[K, P, I]]): Map[I, Matter[K, P, I]]
//}
//
//trait NoCharge[K, P, I] extends Charge[K, P, I] {
//  def energy(neighborhood: Map[I, Matter[K, P, I]]): Double = 0.0
//}
//
//trait NoAction[K, P, I] extends Action[K, P, I] {
//  def transform(neighbornood: Map[I, Matter[K, P, I]]): Map[I, Matter[K, P, I]] = neighbornood
//}
//
//trait MoveAction[K, P, I] extends Action[K, P, I] {
//  def transform(neighborhood: Map[I, Matter[K, P, I]]): Map[I, Matter[K, P, I]] = {
//    // neighborhood.get(this.id).map(p)
//    return neighborhood
//  }
//}
//
//trait BinaryCharge[K, P, I] extends Charge[K, P, I] {
//  def charge: Double
//  override def energy(neighborhood: Map[I, Matter[K, P, I]]): Double = {
//    return 0.0
//  }
//}


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
//  def kind: Char
//  def pos: Int
//  def radius: Int
//  def id: String
//
//  // Distance function TODO: Move to the Position parameter-type of this
//  def dist(other: Particle) = Math.abs(this.pos - other.pos)

  //def transform(neighborhood: Map[String, Particle]): Map[String, Particle]
  def energy(neighborhood: Map[String, Particle]): Double
}



case class StaticParticle(kind: Char, pos: Int, id: String, ver: Long) extends Particle {
  def radius: Int = 0
  //def transform(neighborhood: Map[String, Particle]): Map[String, Particle] = neighborhood
  //def energy(neighborhood: Map[String, Particle]) : Double = 0.0
  def transform(neighborhood: Map[String, Particle]): Map[String, Particle] = neighborhood
  def energy(neighborhood: Map[String, Particle]) : Double = 0.0
  //def next(pos: Int): Matter = this.copy(pos = pos, ver = this.ver+1)

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
  //def next(pos: Int): Matter = this.copy(pos = pos, ver = this.ver + 1)

//  def transform(neighborhood: Map[String, Particle]): Map[String, Particle] = {
//      val deltaPos = Random.nextInt(3) - 1
//      val newParticle = copy(ver = this.ver + 1, pos = this.pos + deltaPos)
//      neighborhood + (newParticle.id -> newParticle)
//  }

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
