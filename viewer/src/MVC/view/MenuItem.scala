package MVC.view

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JMenuItem

class MenuItem(title: String, action: () => Unit) extends JMenuItem(title) {

  this.addActionListener(
    new ActionListener {
      def actionPerformed(arg0: ActionEvent) = action()
    }
  )
}
