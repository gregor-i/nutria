package MVC.view

import javax.swing.JMenu

import MVC.Model

class ModelMenu(f: => String, m: Model) extends JMenu(f) {
  m.addObserver(_ => this.setText(f))
}

class FractalMenu(model: Model) extends ModelMenu("Fractal", model)
class ViewportMenu(model: Model) extends ModelMenu(model.viewport.toString, model)
