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

package nutria.fractal.techniques

import nutria._
import nutria.fractal.{DoubleSequence, SequenceConstructor}
import nutria.syntax._

object ContourTechniques{
  def apply[A <: DoubleSequence:SequenceConstructor]:ContourTechniques[A] = new ContourTechniques[A]
}

class ContourTechniques[A <: DoubleSequence](implicit sequence:SequenceConstructor[A]) {
  // Careful. A lot of strage double magic goes on in this function. ContourCompare is an implementation of the same function to compare for the Mandelbrot sequence.
  def Contour(maxIterations: Int): Fractal =
  (x0, y0) => {
    val seq = sequence(x0, y0, maxIterations)
    var distance = 0d
    for (i <- 0 to maxIterations) {
      seq.next()
      if (seq.publicX.abs > distance)
        distance = seq.publicX.abs
    }
    (distance == Double.PositiveInfinity).fold(0, 1)
  }

  private def ContourCompare(maxIterations: Int): Fractal =
    (x0, y0) => {
      var distance = 10d
      var x = x0
      var y = y0
      for (i <- 0 until maxIterations) {
        val xx = x * x
        val yy = y * y
        y = 2 * x * y + y0
        x = xx - yy + x0

        if (x.abs > distance)
          distance = x.abs
      }
      if (distance == Double.PositiveInfinity)
        1d
      else
        0d
    }
}
