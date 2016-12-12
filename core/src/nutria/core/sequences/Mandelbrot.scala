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

package nutria.core.sequences

import nutria.core.ContentFunction

object Mandelbrot{
  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int, escapeOrbit:Double) extends DoubleSequence {
    private[this] var x: X = 0d
    private[this] var y: Y = 0d
    private[this] var xx = x * x
    private[this] var yy = y * y

    def publicX = x
    def publicY = y

    @inline def hasNext: Boolean = (xx + yy < escapeOrbit) && iterationsRemaining >= 0

    @inline def next(): Boolean = {
      y = 2 * x * y + y0
      x = xx - yy + x0
      xx = x * x
      yy = y * y
      iterationsRemaining -= 1
      hasNext
    }

    @inline override def foldLeft[A](start: (X, Y) => A)(@inline f: (A, X, Y) => A): A = {
      var v = start(x, y)
      while (next()) v = f(v, x, y)
      v
    }
    @inline override def foldLeftX[A](start: X => A)(@inline f: (A, X) => A): A = {
      var v = start(x)
      while (next()) v = f(v, x)
      v
    }

    @inline override def foldLeftY[A](start: Y => A)(@inline f: (A, Y) => A): A = {
      var v = start(y)
      while (next()) v = f(v, y)
      v
    }
  }

  def apply(maxIterations:Int, escapeOrbit:Double):ContentFunction[Sequence] = (x0, y0) => new Sequence(x0, y0, maxIterations, escapeOrbit)
}