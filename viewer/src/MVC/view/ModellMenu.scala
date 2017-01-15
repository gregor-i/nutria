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

package MVC.view

import javax.swing.JMenu

import MVC.model.Model

class ModellMenu(f: => String, val modell: Model) extends JMenu(f) {
  modell.addObserver(() => this.setText(f))
}

class FractalMenu(modell: Model) extends ModellMenu(modell.fractal.getClass.getSimpleName, modell)

class ViewportMenu(modell: Model) extends ModellMenu(modell.view.toString, modell)

class ColorMenu(modell: Model) extends ModellMenu(modell.farbe.toString, modell)

class ContentFactoryMenu(modell: Model) extends ModellMenu(modell.contentFactory.toString, modell)
