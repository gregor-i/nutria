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

import nurtia.data.DimensionInstances
import nurtia.data.consumers.{OrbitPoint, SmoothColoring}
import nurtia.data.sequences.Mandelbrot
import nutria.core.NormalizedContent
import nutria.core.syntax._
import nutria.core.viewport.Viewport

case class Model() {
  val dimensions = DimensionInstances.fullHD

  val transform =
    Viewport.createByDefaultFocusAndLongs(0x3fb9f74a2103c027L, 0x3fe441bdb7277199L, 0x3fba08bcceb6efe6L, 0x3fe440cf7c4a6d3aL)
      .withDimensions(dimensions)


  val content: Symbol Map NormalizedContent[Double] =
    Map(
      'A -> transform
        .withAntiAliasedFractal(Mandelbrot(750, 10) ~> SmoothColoring())
        .strongNormalized,
      'B -> transform
        .withAntiAliasedFractal(Mandelbrot(1500, 10) ~> OrbitPoint(-1.0, 0.0))
        .strongNormalized
    )
}
