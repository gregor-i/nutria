package MVC.view

import javax.swing.JLabel

import MVC.Model

class ModelLabel(f: => String, model: Model) extends JLabel(f) {
  model.addObserver(_ => this.setText(f))
}
