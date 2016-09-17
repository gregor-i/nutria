package MVC.view

import MVC.model.Model
import MVC.controller._
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JMenuItem

import util._

class MenuItem(val title: String,
               val action: () => Unit)
  extends JMenuItem(title) {

  this.addActionListener(
    new ActionListener {
      def actionPerformed(arg0: ActionEvent) = action()
    }
  )
}
