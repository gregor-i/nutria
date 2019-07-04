package MVC.view

import MVC.Model
import javax.swing.JLabel

class ModelLabel(f: => String, model: Model) extends JLabel(f) {
  model.addObserver(_ => this.setText(f))
}
