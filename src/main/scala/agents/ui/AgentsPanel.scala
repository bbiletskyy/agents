package agents.ui

import java.awt.geom.Ellipse2D
import java.awt.{BasicStroke, Color}
import java.awt.image.BufferedImage

import agents.State
import agents.ui.AgentsPanel.LocationSelected

import scala.swing.event.{Event, MouseClicked}
import scala.swing.{Dimension, Graphics2D, Panel}
import scala.util.Random

class AgentsPanel(val w: Int = 200, val h: Int = 200) extends Panel {
  preferredSize = new Dimension(w, h)
  val bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR)
  listenTo(mouse.clicks)
  reactions += {
    case e: MouseClicked => publish(LocationSelected(e.point.x, e.point.y))
  }

  override def paintComponent(g: Graphics2D) {
    super.paintComponent(g)
    g.drawImage(bufferedImage, null, 0, 0)
  }

  def drawAgents(states: List[State]) = {
    val g = bufferedImage.createGraphics();

    g.setColor(Color.lightGray)
    g.fillRect(0, 0, h, w)

    states.foreach(drawAgent(_, g))

    super.repaint()
  }

  def drawAgent(state: State, g: Graphics2D): Unit = {
    val r = 2
    val rr = 6
    g.setColor(Color.MAGENTA)
    g.fill(new Ellipse2D.Double(state.location.x - r, state.location.y-r, 2*r, 2*r))

    g.setColor(Color.BLUE)
    g.setStroke(new BasicStroke(1f))
    g.draw(new Ellipse2D.Double(state.location.x - rr, state.location.y - rr, 2 * rr, 2 * rr))
  }
}

object AgentsPanel {
  case class LocationSelected(x: Double, y: Double) extends Event
}