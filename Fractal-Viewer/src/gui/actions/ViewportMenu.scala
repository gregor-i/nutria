package gui.actions

import MVC.model.Model
import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuItem
import entities.viewport._

class ViewportMenu(views: Array[Viewport], gui: Model) extends JMenu("Viewports") {

  private def menu_order(s: Array[String], a: Array[ActionListener]) {
    if (s.length > 10) {
      var sub = new JMenu("Sub")
      for (i <- 0 until s.length) {
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
      for (i <- 0 until s.length) {
        val menuItem = new JMenuItem(s(i))
        menuItem.addActionListener(a(i))
        add(menuItem)
      }
    }
  }

  val reset = new JMenuItem("Reset")

  reset.addActionListener(new ViewportAction(gui, ViewportUtil.start))

  add(reset)

  val names = Array.ofDim[String](views.length)

  val actions = Array.ofDim[ActionListener](views.length)

  for (i <- 0 until views.length) {
    names(i) = java.lang.Integer.toString(i)
    actions(i) = new ViewportAction(gui, views(i))
  }

  menu_order(names, actions)
}
