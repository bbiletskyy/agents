package type_playing

import java.util.UUID

import scala.util.Random

object Particles extends App {
  println("Hello")
  val p = Active('*', 0)
  val s = Static('|', 1)
  println(s"$p")
  val neighborhood = Map(p.id -> p)
  val newNeighborhood = p.transform(neighborhood)
  val newParticle = newNeighborhood.values.head
  println(s"$newParticle")

}

trait Matter[P <: Matter[P]] {
  def kind: Char
  def id: String
  def pos: Double
  def next(pos: Double): P
}

trait Action[P <: Matter[P]] extends Matter[P] {
  def aRadius: Double
  def interact(neighborhood: Map[String, P]): Map[String, P]
}

trait Charge [P <: Matter[P]] extends Matter[P] {
  def cRadius: Double
  def energy(neighborhood: Map[String, P]): Double
}

trait Transformation[P <: Matter[P]] extends Matter[P] {
  def ver: Long
  def radius: Double
  def transform(neighborhood: Map[String, P]): Map[String, P]
}

trait FloatAction[P <: Matter[P]] extends Action[P] {
  def interact(neighborhood: Map[String, P]): Map[String, P] = {
    val deltaPos = aRadius * (if(Random.nextBoolean()) -1 else 1)
    val newParticle = next(pos = this.pos + deltaPos)
    neighborhood + (newParticle.id -> newParticle)
  }
}

trait DummyCharge [P <: Matter[P]] extends Charge[P] {
  def amount: Double
  def energy(neighborhood: Map[String, P]): Double = {
    amount
  }
}

trait DummyTransformation[P <: Particle[P]] extends Transformation[P] {
  def transform(neighborhood: Map[String, P]): Map[String, P] = {
    val particle = neighborhood(this.id)
    val newNeighborhood = particle.interact(neighborhood)
    newNeighborhood
  }
}

trait Particle[P <: Matter[P]] extends Matter[P] with Action[P] with Charge[P] with Transformation[P] {
  def radius: Double = Math.max(aRadius, cRadius)
}

case class Active(kind: Char, pos: Double, aRadius: Double, cRadius: Double, amount: Double, ver: Long, id: String)
  extends Particle[Active]
  with FloatAction[Active]
  with DummyCharge[Active]
  with DummyTransformation[Active] {
  def next(pos: Double): Active = copy(pos = pos, ver = this.ver+1)
}

object Active {
  def apply(kind: Char, pos: Double, aRadius: Double = 1, cRadius: Double = 1, amount: Double= 7, ver: Long = 0L, id: String = UUID.randomUUID().toString): Active = new Active(kind, pos, aRadius, cRadius, amount, ver, id)
}

case class Static(kind: Char, pos: Double, aRadius: Double, cRadius: Double, amount: Double, ver: Long, id: String)
  extends Particle[Static]
    with FloatAction[Static]
    with DummyCharge[Static]
    with DummyTransformation[Static] {
  def next(pos: Double): Static = copy(pos = pos, ver = this.ver+1)
}

object Static {
  def apply(kind: Char, pos: Double, aRadius: Double=0, cRadius: Double=0, amount: Double = 0, ver: Long=0L, id: String = UUID.randomUUID().toString): Static = new Static(kind, pos, aRadius, cRadius, amount, ver, id)
}
