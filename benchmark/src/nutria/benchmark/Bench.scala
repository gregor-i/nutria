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

import nutria.content._
import nutria.fractal._
import nutria.fractal.alternativeImplementions.{SpireBrot, StreamBrot}
import nutria.fractal.techniques.EscapeTechniques
import nutria.syntax._
import nutria.viewport._
import org.openjdk.jmh.annotations.Benchmark
import spire.math.Quaternion

class Bench {
  def operation(fractal: Fractal): CachedContent =
    Mandelbrot.start
      .withDimensions(Dimensions.fujitsu.scale(0.1))
      .withAntiAliasedFractal(fractal)
      .cached

  @Benchmark
  def mandelRough = operation(EscapeTechniques[Mandelbrot.Sequence].RoughColoring(500))

  @Benchmark
  def spireRough = operation(EscapeTechniques[SpireBrot.Sequence].RoughColoring(500))

  @Benchmark
  def streamRough = operation(StreamBrot.RoughColoring(500))

  @Benchmark
  def quatbRough = operation{
    val quat = new QuaternionBrot((x, y) => Quaternion(x, y, 0, 0))
    import quat.seqConstructor
    EscapeTechniques[quat.Sequence].RoughColoring(500)
  }

}
