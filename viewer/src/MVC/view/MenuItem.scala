package MVC.view

import javax.swing.JMenuItem

class MenuItem(title: String, action: () => Unit) extends JMenuItem(title) {
  this.addActionListener(_ => action())
}
