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

import MVC.model._
import MVC.view._
import nutria.consumers.SmoothColoring
import nutria.sequences.JuliaSet
import nutria.syntax._

object Viewer {
  def main(args: Array[String]): Unit = {


    val model = new Model()
//    model.setFractal(TrapTechniques[Mandelbrot.Sequence].SmoothColoring(500))

    model.setSequence(Some(JuliaSet(-0.6, 0.6)(50)))
    model.setFractal(JuliaSet(-0.6, 0.6)(500) ~> SmoothColoring())
    new View(model)
  }
}


