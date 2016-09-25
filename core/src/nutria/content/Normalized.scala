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
package content

import nutria.accumulator.{Max, Min}

trait Normalized

case class LinearNormalizedContent(content: CachedContent) extends Content with Normalized {
  val dimensions = content.dimensions

  private val max = content.applyAccumulator(Max)
  private val min = content.applyAccumulator(Min)
  require(max != min)
  private val dy: Double = 1.0 / (max - min)
  private val y0: Double = -min / (max - min)

  @inline private def clamp(v: Double, max: Double, min: Double) =
    if (v > max) max
    else if (v < min) min
    else v

  def apply(x: Int, y: Int): Double = {
    clamp(y0 + content(x, y) * dy, 1, 0)
  }
}

case class StrongNormalizedContent(content: CachedContent) extends Content with Normalized {
  val dimensions = content.dimensions

  private val map = (for (x <- 0 until width; y <- 0 until height)
    yield content(x, y) -> (x, y)).par.groupBy(_._1).mapValues(_.map(_._2))

  private val sorted = map.seq.toSeq.sortBy(_._1)

  private val values = Array.fill[Double](width, height)(0d)

  private var finished = 0
  
  for ((_, pos) <- sorted) {
    for ((x, y) <- pos) {
      values(x)(y) = finished.toDouble / (width * height)
    }
    finished += pos.size
  }

  override def apply(x: Int, y: Int): Double = values(x)(y)
}
