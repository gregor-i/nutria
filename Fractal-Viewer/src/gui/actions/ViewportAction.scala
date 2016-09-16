package gui.actions

import MVC.model.Model
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import entities.viewport.Viewport

class ViewportAction(private val gui: Model, private val view: Viewport) extends ActionListener {

  override def actionPerformed(e: ActionEvent) {
    gui.setViewport(view)
  }
}
