package agents

import java.util.UUID

import scala.util.Random

case class Location(x:Double=0.0, y: Double=0.0) {
  def to(other: Location): Double = {
    val dx = (this.x - other.x)
    val dy = (this.y - other.y)
    math.sqrt(dx * dx + dy * dy)
  }
}
case class State(kind: String, location: Location, energy: Double, id: String, version: Long) {

  def nextVersion(): State = this.copy(version = this.version)
  def increaseEnergy(deltaEnergy: Double) = this.copy(energy = this.energy + deltaEnergy)

  def fieldEnergy(other: State): Double = {
    val r = this.location.to(other.location)
    return r
  }

  def deltaFieldEnergy(otherPrevState: State, otherNextState: State): Double = {
    return fieldEnergy(otherNextState) - fieldEnergy(otherPrevState)
  }

  def transform(): State = {
      val res = this.nextVersion()
      val dx = Random.nextGaussian()
      val dy = Random.nextGaussian()
      res.copy(energy = 0.0, location=Location(res.location.x + dx, res.location.y + dy))
  }
}
object State {
  def apply(kind: String, x: Double, y: Double, energy: Double): State = State(kind, Location(x, y), energy, UUID.randomUUID().toString, 0)
  def apply(kind: String, x: Double, y: Double): State = State(kind, x, y, 0.0)
}