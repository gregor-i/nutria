package MVC.view

import MVC.model.Model
import javax.swing.JMenu
import util._

class ModellMenu(f: => String, val modell: Model) extends JMenu(f) {

  modell.addObserver(() => this.setText(f))
}

class FractalMenu(modell: Model) extends ModellMenu(modell.fractal.getClass.getSimpleName, modell) {}

class ViewportMenu(modell: Model) extends ModellMenu(modell.view.toString(), modell) {}

class ColorMenu(modell: Model) extends ModellMenu(modell.farbe.toString(), modell) {}

class ContentFactoryMenu(modell: Model) extends ModellMenu(modell.contentFactory.toString(), modell) {}
