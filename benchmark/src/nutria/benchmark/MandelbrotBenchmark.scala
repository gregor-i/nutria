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

import nurtia.data.DimensionInstances
import nurtia.data.consumers.RoughColoring
import nurtia.data.directFractals.alternativeImplementation.StreamBrot
import nurtia.data.fractalFamilies.MandelbrotData
import nurtia.data.sequences.alternativeImlementations.SpireBrot
import nurtia.data.sequences.{Mandelbrot, QuaternionBrot}
import nutria.core.ContentFunction
import nutria.core.content.CachedContent
import nutria.core.syntax._
import org.openjdk.jmh.annotations.Benchmark
import spire.math.Quaternion

class MandelbrotBenchmark {
  def operation(fractal: ContentFunction[Double]): CachedContent[Double] =
    MandelbrotData.initialViewport
      .withDimensions(DimensionInstances.fujitsu.scale(0.1))
      .withAntiAliasedFractal(fractal)
      .cached

  @Benchmark
  def mandelRough = operation(Mandelbrot(500, 4d) ~> RoughColoring.double())

  @Benchmark
  def spireRough = operation(SpireBrot(500) ~> RoughColoring.double())

  @Benchmark
  def streamRough = operation(StreamBrot.RoughColoring(500))

  @Benchmark
  def quatbRough = operation(QuaternionBrot((x, y) => Quaternion(x, y, 0, 0))(500) ~> RoughColoring.double())
}
