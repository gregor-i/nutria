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

package nutria.fractal

trait AbstractSequence {
  @inline def hasNext: Boolean

  @inline def next(): Boolean

  @inline def size(): Int = {
    var i = 0
    while (next()) i = i + 1
    i
  }
}

trait DoubleSequence extends AbstractSequence { self =>
  type X = Double
  type Y = Double

  def publicX: X
  def publicY: X
  def public: (X, Y) = (publicX, publicY)

  @inline def foldLeft(start: Double)(@inline f: (Double, X, Y) => Double): Double

  @inline def foldLeftX(start: Double)(@inline f: (Double, X) => Double): Double

  @inline def foldLeftY(start: Double)(@inline f: (Double, Y) => Double): Double

  def wrapped: scala.Iterator[(X, Y)] =
    new scala.Iterator[(X, Y)] {
      override def hasNext: Boolean = self.hasNext

      override def next(): (X, Y) = {
        self.next()
        public
      }
    }
}