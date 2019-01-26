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
//    if (title == "Start") {
//      title == "Stop"
//      agentManagement.start()
//    } else if (title == "Stop") {
//      title == "Start"
//      agentManagement.stop()
//    }
//    this.title = "Stop"
    agentManagement.start()
    //agents ! Start
  }
  val refreshButton = Button("Stop") { agentManagement.stop() }

  listenTo(agentsPanel)
  reactions += {
    case LocationSelected(x, y) => println("Location selected")
  }
  contents = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Look at me!")
    contents += agentsPanel
    contents += new BoxPanel(Orientation.Horizontal) {
      contents += startStopButton
      contents += refreshButton
    }

  }

  def updateAgents(states: List[State]): Unit = {
    agentsPanel.drawAgents(states)
  }
}




object UIFrame extends MainFrame {
//  def main(args: Array[String]) {
//    val ui = new UIFrame()
//    ui.visible = true
//    println("End of main function")
//  }


}
