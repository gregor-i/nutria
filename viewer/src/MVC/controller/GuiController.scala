/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package MVC.controller

import java.awt.event._

import MVC.model.Model
import MVC.view.View
import nurtia.data.fractalFamilies.MandelbrotData
import nutria.core.colors.Invert
import nutria.core.syntax._
import nutria.core.viewport.{Dimensions, Point}

class GuiController(val modell: Model, val view: View) extends KeyListener with MouseListener with MouseWheelListener with MouseMotionListener {
  val imgPanel = view.imgPanel
  imgPanel.addMouseListener(this)
  imgPanel.addMouseWheelListener(this)
  imgPanel.addMouseMotionListener(this)
  imgPanel.addKeyListener(this)

  override def keyReleased(arg0: KeyEvent) = ()
  override def keyTyped(arg0: KeyEvent) = ()
  override def mouseClicked(e: MouseEvent) = ()
  override def mouseEntered(e: MouseEvent) = ()
  override def mouseExited(e: MouseEvent) = ()
  override def mouseReleased(e: MouseEvent) = ()

  override def mousePressed(e: MouseEvent) = {
    /*val trans = modell.view.withDimensions(Dimensions(imgPanel.getWidth, imgPanel.getHeight))
    val x = trans.transformX(e.getX, e.getY)
    val y = trans.transformY(e.getX, e.getY)
    println(s"Clicked in ($x, $y)")
    println(s"minDist = ${CardioidNumeric.minimalDistance(10)(x, y)}")*/
    val px = e.getX / imgPanel.getWidth.toDouble
    val py = e.getY / imgPanel.getHeight.toDouble
    modell.setViewport(modell.view.focus(px, py))
  }

  class DocAction(descr: String, run: => Unit) {
    def unsafePerformIO: Unit = run
    override def toString(): String = descr
  }
  val keyCodePretty: Map[Int, String] = Map(
    KeyEvent.VK_DOWN     -> "Down",
    KeyEvent.VK_UP       -> "Up",
    KeyEvent.VK_RIGHT    -> "Right",
    KeyEvent.VK_LEFT     -> "Left",
    KeyEvent.VK_ADD      -> "+",
    KeyEvent.VK_SUBTRACT -> "-",
    KeyEvent.VK_I        -> "I",
    KeyEvent.VK_ENTER    -> "Enter",
    KeyEvent.VK_S        -> "S",
    KeyEvent.VK_R        -> "R",
    KeyEvent.VK_H        -> "H"
  ).withDefaultValue("Undefined")

  val keyMap: Map[Int, DocAction] = Map(
    KeyEvent.VK_DOWN      -> new DocAction("Move viewport down", modell.setViewport(modell.view.down())),
    KeyEvent.VK_UP        -> new DocAction("Move viewport up", modell.setViewport(modell.view.up())),
    KeyEvent.VK_RIGHT     -> new DocAction("Move viewport right", modell.setViewport(modell.view.right())),
    KeyEvent.VK_LEFT      -> new DocAction("Move viewport left", modell.setViewport(modell.view.left())),
    KeyEvent.VK_ADD       -> new DocAction("Zoom in", modell.setViewport(modell.view.zoomIn())),
    KeyEvent.VK_SUBTRACT  -> new DocAction("Zoom out", modell.setViewport(modell.view.zoomOut())),
    KeyEvent.VK_I         -> new DocAction("Invert colors", modell.setColor(Invert.invert(modell.farbe))),
    KeyEvent.VK_ENTER     -> new DocAction("Print current position", println(modell.view.toString)),
    KeyEvent.VK_S         -> new DocAction("Renders the current view in HD", modell.snap()),
    KeyEvent.VK_R         -> new DocAction("Reset viewport", modell.setViewport(MandelbrotData.initialViewport)),
    KeyEvent.VK_H         -> new DocAction("Display this help", println(keyMap map { case (keyCode, docu) => s"[${keyCodePretty(keyCode)}]\t $docu"} mkString "\n"))
  ).withDefaultValue(new DocAction("Undefined", ()))

  override def keyPressed(arg0: KeyEvent) =
    arg0.getKeyCode match { case whatever => keyMap(whatever).unsafePerformIO }

  override def mouseWheelMoved(e: MouseWheelEvent) = {
    val px = e.getX / imgPanel.getWidth.toDouble
    val py = e.getY / imgPanel.getHeight.toDouble
    modell.setViewport(modell.view.zoom(px, py, e.getWheelRotation))
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    val trans = modell.view.withDimensions(Dimensions(imgPanel.getWidth, imgPanel.getHeight))
    val x = trans.transformX(e.getX, e.getY)
    val y = trans.transformY(e.getX, e.getY)

    modell.stateString = s"MousePosition = ($x, $y)"

    modell.sequenceConstructor
      .map(_.apply(x, y))
      .map(_.wrapped)
      .map(points => Point(x, y) +: points.filter(t => t._1 == t._1 && t._2 == t._2).map(Point.tupled).toSeq)
      .foreach(modell.setPoints)
  }
  override def mouseDragged(e: MouseEvent): Unit = ()
}
