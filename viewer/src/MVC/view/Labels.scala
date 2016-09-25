package MVC.view

import MVC.model.Model
import javax.swing.JLabel
import util._

class ModellLabel(f: => String, modell: Model) extends JLabel(f) {

  modell.addObserver(() => this.setText(f))
}

class FractalLabel(modell: Model) extends ModellLabel(modell.fractal.toString(), modell) {}

class ViewportLabel(modell: Model) extends ModellLabel(modell.view.toString(), modell) {}

class ColorLabel(modell: Model) extends ModellLabel(modell.farbe.toString(), modell) {}

class ContentFactoryLabel(modell: Model) extends ModellLabel(modell.contentFactory.toString(), modell) {}