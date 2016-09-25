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