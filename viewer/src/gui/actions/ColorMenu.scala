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

import java.awt.event.ActionListener
import javax.swing.{JMenu, JMenuItem}

import MVC.model.Model
import nutria.core.Color
import nutria.core.color.HSV

class ColorMenu(farben: List[Color[Double]], gui: Model) extends JMenu("Farben") {

  private def menu_order(s: Array[String], a: Array[ActionListener]) {
    if (s.length > 10) {
      var sub = new JMenu("Sub")
      for (i <- s.indices) {
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
      for (i <- s.indices) {
        val menuItem = new JMenuItem(s(i))
        menuItem.addActionListener(a(i))
        add(menuItem)
      }
    }
  }

  val reset = new JMenuItem("Default")
  reset.addActionListener(new Action(gui.setColor(HSV.MonoColor.Blue)))
  add(reset)
  val names = Array.ofDim[String](farben.length)
  val actions = Array.ofDim[ActionListener](farben.length)

  for (i <- farben.indices) {
    names(i) = farben(i).toString
    actions(i) = new Action(gui.setColor(farben(i)))
  }
  menu_order(names, actions)
}
