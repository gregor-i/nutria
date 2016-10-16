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

package nutria.core.content

import nutria.core.accumulator.{Accumulator, Variance}
import nutria.core.{Fractal, Transform}

@deprecated("not working", ".")
case class DynamAntiAliasFractalContent(fractal: Fractal, transform: Transform,
                                        accumulator: Accumulator, limit: Double, minimalIterations: Int, maximalIterations: Int)
  extends Content[Double] {
  val dimensions = transform.dimensions

  val phi = (Math.sqrt(5) + 1) / 2
  val phi_loc = phi * width
  val phiphi_loc = phi * phi * height

  override def apply(x: Int, y: Int): Double = {
    val (x0, y0) = transform.transform(x, y)
    var (rx, ry) = (0.0, 0.0)
    var varianceState = Variance.neutral
    var accuState = accumulator.neutral
    var n = 0
    while (n <= minimalIterations || (n <= maximalIterations && Variance.lastOperation(varianceState, n) > limit * n)) {
      rx = rx + phi_loc
      ry = ry + phiphi_loc
      val f = fractal(x0 + rx % transform.scaleX + ry % transform.shearX,
                      y0 + rx % transform.shearY + ry % transform.scaleY)
      varianceState = Variance.fold(varianceState, f)
      accuState = accumulator.fold(accuState, f)
      n += 1
    }
    accumulator.lastOperation(accuState, n)
  }
}
