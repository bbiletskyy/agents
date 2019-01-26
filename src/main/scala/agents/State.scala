package agents

case class Location(x:Double=0.0, y: Double=0.0) {
  def to(other: Location): Double = {
    val dx = (this.x - other.x)
    val dy = (this.y - other.y)
    math.sqrt(dx * dx + dy * dy)
  }
}
case class State(kind: String, location: Location=Location(), energy: Double = 0.0)
object State {
  def apply(kind: String, x: Double, y: Double, energy: Double): State = State(kind, Location(x, y), energy)
}