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

package gui.actions

import MVC.model.Model
import java.awt.event.ActionListener
import javax.swing.JMenu
import javax.swing.JMenuItem

import nutria.fractal.Mandelbrot
import nutria.viewport._

class ViewportMenu(views: Array[Viewport], gui: Model) extends JMenu("Viewports") {

  private def menu_order(s: Array[String], a: Array[ActionListener]) {
    if (s.length > 10) {
      var sub = new JMenu("Sub")
      for (i <- 0 until s.length) {
        val menuItem = new JMenuItem(s(i))
        menuItem.addActionListener(a(i))
        sub.add(menuItem)
        if (i % 10 == 9) {
          add(sub)
          sub = new JMenu("Sub")
        }
      }
      add(sub)
    } else {
      for (i <- 0 until s.length) {
        val menuItem = new JMenuItem(s(i))
        menuItem.addActionListener(a(i))
        add(menuItem)
      }
    }
  }

  val reset = new JMenuItem("Reset")

  reset.addActionListener(new ViewportAction(gui, Mandelbrot.start))

  add(reset)

  val names = Array.ofDim[String](views.length)

  val actions = Array.ofDim[ActionListener](views.length)

  for (i <- 0 until views.length) {
    names(i) = java.lang.Integer.toString(i)
    actions(i) = new ViewportAction(gui, views(i))
  }

  menu_order(names, actions)
}
