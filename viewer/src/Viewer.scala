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
import nutria.core.consumers.OrbitBothAxis
import nutria.core.image.{DefaultSaveFolder, SaveFolder}
import nutria.core.sequences.Mandelbrot
import nutria.core.syntax._

object Viewer {
  def main(args: Array[String]): Unit = {

    implicit val saveFolder = DefaultSaveFolder / "viewer"
    val model = Model.default
//    model.setFractal(TrapTechniques[Mandelbrot.Sequence].SmoothColoring(500))

//    model.setSequence(Some(JuliaSet(-0.6, 0.6)(50)))
//    model.setFractal(JuliaSet(-0.6, 0.6)(500) ~> SmoothColoring())
    model.setFractal(Mandelbrot(350, 10000) ~> OrbitBothAxis())
    new View(model)
  }
}


