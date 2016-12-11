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

package nutria.benchmark

import nurtia.data.MandelbrotData
import nutria.core.syntax._
import nutria.core._
import nutria.core.content.CachedContent
import nutria.core.sequences.Mandelbrot
import nutria.core.viewport.Dimensions
import org.openjdk.jmh.annotations.Benchmark

object NormalizationBenchmark{
  val exampleCachedContent: CachedContent[Double] =
    MandelbrotData.initialViewport
      .withDimensions(Dimensions.fujitsu.scale(0.1))
      .withFractal(Mandelbrot(350, 4) ~> consumers.SmoothColoring())
      .cached
}

class NormalizationBenchmark {
//  @Benchmark
//  def linear = NormalizationBenchmark.exampleCachedContent.linearNormalized.cached

  @Benchmark
  def strong = NormalizationBenchmark.exampleCachedContent.strongNormalized.cached
}