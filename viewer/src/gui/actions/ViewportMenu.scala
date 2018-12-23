package gui.actions

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JMenu, JMenuItem}

import MVC.Model
import nutria.data.fractalFamilies.MandelbrotFamily
import nutria.core.Viewport

class ViewportMenu(views: Array[Viewport], gui: Model) extends JMenu("Viewports") {

  private def menu_order(s: Array[String], a: Array[ActionListener]) {
    if (s.length > 10) {
      var sub = new JMenu("Sub")
      for (i <- s.indices) {
        val menuItem = new JMenuItem(s(i))
        menuItem.addActionListener(a(i))
        sub.add(menuItem)
        if (i % 10 == 9) {
          add(sub)
          sub = new JMenu("Sub")
        }
      }
      add(sub)
    } else {
      for (i <- s.indices) {
        val menuItem = new JMenuItem(s(i))
        menuItem.addActionListener(a(i))
        add(menuItem)
      }
    }
  }

  val reset = new JMenuItem("Reset")

  reset.addActionListener(_ => gui.viewport = MandelbrotFamily.initialViewport)

  add(reset)

  val names = Array.ofDim[String](views.length)

  val actions = Array.ofDim[ActionListener](views.length)

  for (i <- views.indices) {
    names(i) = java.lang.Integer.toString(i)
    actions(i) = _ => gui.viewport = views(i)
  }

  menu_order(names, actions)
}
