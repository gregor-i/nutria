package MVC.view

import MVC.model.Model
import javax.swing.JMenu
import util._

class ModellMenu(val f: (Model => String), val modell: Model)
  extends JMenu(f(modell))
  with Observer {

  modell.addObserver(ModellMenu.this)

  override def update(caller: Observable) = {
    ModellMenu.this.setText(f(modell))
  }
}

class FractalMenu(modell: Model) extends ModellMenu((modell) => modell.fractal.toString(), modell) {}
class ViewportMenu(modell: Model) extends ModellMenu((modell) => modell.view.toString(), modell) {}
class ColorMenu(modell: Model) extends ModellMenu((modell) => modell.farbe.toString(), modell) {}
class ContentFactoryMenu(modell: Model) extends ModellMenu((modell) => modell.contentFactory.toString(), modell) {}
