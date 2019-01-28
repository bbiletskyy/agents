package agents.ui
import agents.State

import scala.swing._
import agents.ui.AgentsPanel.LocationSelected




class UIFrame(val agentManagement: AgentManagement) extends MainFrame {
  visible = true
  title = "Agents"
  preferredSize = new Dimension(320, 240)
  val agentsPanel = new AgentsPanel(200, 200)
  val startStopButton = Button("Start") {
    agentManagement.start()
  }
  val stopButton = Button("Stop") { agentManagement.stop() }
  val stepButton = Button("Step") { agentManagement.step() }

  listenTo(agentsPanel)
  reactions += {
    case LocationSelected(x, y) => println("Location selected")
  }
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Look at me!")
    contents += agentsPanel
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += startStopButton
      contents += stopButton
      contents += stepButton
    }

  }

  def updateAgents(states: List[State]): Unit = {
    agentsPanel.drawAgents(states)
  }
}


