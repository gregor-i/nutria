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

import nutria.core.accumulator.{Max, Min}

trait Normalized

case class LinearNormalizedContent(content: CachedContent[Double]) extends Content[Double] with Normalized {
  val dimensions = content.dimensions

  private val max = Max(content.values.flatten)
  private val min = Min(content.values.flatten)
  require(max != min)
  private val dy: Double = 1.0 / (max - min)
  private val y0: Double = -min / (max - min)

  def apply(x: Int, y: Int): Double = {
    y0 + content(x, y) * dy
  }
}

private object StrongNormalizedContentHelper {
  def apply[A: Ordering](content: CachedContent[A]): Seq[Seq[Double]] = {
    val width = content.width
    val height = content.height

    val swappedAndSorted = (for {
      x <- 0 until width
      y <- 0 until height
    } yield content(x, y) ->(x, y)).toList.sortBy(_._1)

    val values = Array.fill[Double](width, height)(0d)

    var list = swappedAndSorted
    var finished = 0
    while (list.nonEmpty) {
      val headKey = list.head._1
      val (part, remaining) = list.span(_._1 == headKey)
      val partSize = part.size

      val value = (finished + partSize / 2d) / (width * height)
      part.foreach { case (_, (x, y)) => values(x)(y) = value }

      list = remaining
      finished += partSize
    }
    assert(list == Nil)
    assert(finished == width * height)

    values.map(_.toSeq)
  }
}

case class StrongNormalizedContent[A: Ordering](content: CachedContent[A])
  extends CachedContent[Double](StrongNormalizedContentHelper(content), content.dimensions)
    with Normalized