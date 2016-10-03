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

package nutria
package benchmark

import nurtia.data.MandelbrotData
import nutria.core.Fractal
import nutria.core.consumers.RoughColoring
import nutria.core.content.CachedContent
import nutria.core.directFractals.alternativeImplementation.StreamBrot
import nutria.core.sequences.alternativeImlementations.SpireBrot
import nutria.core.sequences.{Mandelbrot, QuaternionBrot}
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import org.openjdk.jmh.annotations.Benchmark
import spire.math.Quaternion

class Bench {
  def operation(fractal: Fractal): CachedContent =
    MandelbrotData.initialViewport
      .withDimensions(Dimensions.fujitsu.scale(0.1))
      .withAntiAliasedFractal(fractal)
      .cached

  @Benchmark
  def mandelRough = operation(Mandelbrot(500, 4d) ~> RoughColoring())

  @Benchmark
  def spireRough = operation(SpireBrot(500) ~> RoughColoring())

  @Benchmark
  def streamRough = operation(StreamBrot.RoughColoring(500))

  @Benchmark
  def quatbRough = operation(QuaternionBrot((x, y) => Quaternion(x, y, 0, 0))(500) ~> RoughColoring())
}
