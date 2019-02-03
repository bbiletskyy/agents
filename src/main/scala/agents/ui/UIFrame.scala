package agents.ui
import agents.State

import scala.swing._
import agents.ui.AgentsPanel.LocationSelected




class UIFrame(val agentManagement: AgentManagement) extends MainFrame {
  visible = true
  title = "Agents"
  preferredSize = new Dimension(600, 600)
  val agentsPanel = new AgentsPanel(500, 500)
  val startStopButton = Button("Start") {
    agentManagement.start()
  }
  val stopButton = Button("Stop") { agentManagement.stop() }
  val stepButton = Button("Step") { agentManagement.step() }

  listenTo(agentsPanel)
  reactions += {
    case LocationSelected(x, y) => println("Location selected")
  }
  val timeLabel = new Label("Time: 0")

  contents = new BoxPanel(Orientation.Vertical) {
    contents += timeLabel
    contents += agentsPanel
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += startStopButton
      contents += stopButton
      contents += stepButton
    }

  }

  def updateAgents(tickCount: Long, states: List[State]): Unit = {
    val energies = states.map(_.energy)
    def round(d: Double) = d - (d % 0.001)
    val meanEnergy = round(energies.sum/Math.max(states.size, 1))
    val minEnergy = round(energies.min)
    val maxEnergy = round(energies.max)
    timeLabel.text_=(s"Time: $tickCount, mean: $meanEnergy, min: $minEnergy, max: $maxEnergy")
    agentsPanel.drawAgents(states)
  }
}

object UIFrame extends App {
  val d: Double = 13.07/7.08
  println(d - (d %0.01))
}

