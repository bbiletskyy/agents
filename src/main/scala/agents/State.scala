package agents

import java.util.UUID

import scala.util
import scala.util.Random


case class State(charge: Charge, id: String, version: Long) {

  def nextVersion(): State = this.copy(version = this.version + 1)

  def energy(): Double = 0.0

  def transform(): State = {
      val res = this.nextVersion()
      res.copy(charge = this.charge.copy(location = this.charge.location.randomStep()))
  }

  def location: Location = charge.location
}


object State {
  def apply(id: String, kind: String, x: Double, y: Double): State = State(Charge(kind, x, y), id, 1)
  //def apply(kind: String, x: Double, y: Double, energy: Double): State = State(UUID.randomUUID().toString, kind, Location(x, y), energy, 0)
  //def apply(kind: String, x: Double, y: Double): State = State(kind, x, y, 0.0)
  //def apply(id: String, kind: String, x: Double, y: Double): State = State(id, kind, Location(x, y), 0.0, 0)
}


case class Charge(kind: String, location: Location, amount: Double)

object Charge {
  def apply(kind: String, x: Double, y: Double): Charge = Charge(kind = kind, location = Location(x, y), amount = 1.0)
}

case class Location(x: Double=0.0, y: Double=0.0) {
  def to(other: Location): Double = {
    val dx = (this.x - other.x)
    val dy = (this.y - other.y)
    math.sqrt(dx * dx + dy * dy)
  }

  def randomStep(maxAxisStep: Double = 1): Location = {
    val dx = maxAxisStep * Random.nextFloat()
    val dy = maxAxisStep * Random.nextFloat()
    this.copy(x = x + dx, y = y + dy)
  }
}