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

import nutria.core.Dimensions
import nutria.core.viewport.HasDimensions

trait Content[A] extends HasDimensions {
  def apply(x: Int, y: Int): A
}

class CachedContent[A](val values: Seq[Seq[A]], val dimensions: Dimensions) extends Content[A] {
  def this(content: Content[A]) =
    this(
      (0 until content.width).par.map(x => (0 until content.height).map(y => content(x, y))).seq,
      content.dimensions)

  override def apply(x: Int, y: Int): A = values(x)(y)

  def map[B](f: A => B): CachedContent[B] = new CachedContent[B](values.map(_.map(f)), dimensions)
}
