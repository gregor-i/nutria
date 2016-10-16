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

import nutria.core.accumulator.Accumulator
import nutria.core.{Fractal, Transform}

// Implements SSAA/FSAA
case class AntiAliasedFractalContent(fractal: Fractal, transform: Transform,
  accu: Accumulator, multi: Int)
  extends Content[Double] {

  val dimensions = transform.dimensions

  private val factor = 1.0 / multi

  def apply(x_i: Int, y_i: Int): Double =
    accu(
      for {
        x_s <- 0 until multi
        y_s <- 0 until multi
        x = transform.transformX(x_i, y_i, x_s, y_s, factor)
        y = transform.transformY(x_i, y_i, x_s, y_s, factor)
      } yield fractal(x, y)
    )
}

