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

import nutria.viewport.HasDimensions

trait Content extends HasDimensions {
  def apply(x: Int, y: Int): Double
}

object CachedContent{
  def cache(content: Content): Seq[Seq[Double]] =
    (0 until content.width).par.map(x => (0 until content.height).map(y => content(x, y))).seq
}

class CachedContent(val values: Seq[Seq[Double]], val dimensions: Dimensions) extends Content {
  def this(content: Content) = this(CachedContent.cache(content), content.dimensions)

  override def apply(x: Int, y: Int): Double = values(x)(y)

  def applyAccumulator(accu: Accumulator): Double =
    accu(values.flatten)
}
