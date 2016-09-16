package MVC.view

import MVC.model.Model
import java.awt.event.ActionListener
import java.awt.event.ActionEvent

import MVC.ContentFactory
import entities.Fractal
import util._

class Action(val action: (Model => Unit),
             val modell: Model)
  extends ActionListener{

  override def actionPerformed(arg0: ActionEvent) {
    action(modell)
  }
}

class SetFractalAction(fractal: Fractal, modell: Model) extends Action(
  (modell => modell.setFractal(fractal)),
  modell) {}

import entities.viewport.Viewport
class SetViewportAction(view: Viewport, modell: Model) extends Action(
  (modell => modell.setViewport(view)),
  modell) {}

import entities.color.Color
class SetColorAction(farbe: Color, modell: Model) extends Action(
  (modell => modell.setColor(farbe)),
  modell) {}

class SetContentFactoryAction(beauty: ContentFactory, modell: Model) extends Action(
  (modell => modell.setImageFactory(beauty)),
  modell) {}