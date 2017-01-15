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

package nurtia.data.sequences

import nutria.core.{ContentFunction, DoubleSequence, MathUtils}

object MandelbrotCube extends MathUtils {

  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var x: X = 0d
    private[this] var y: Y = 0d
    def publicX = x
    def publicY = y

    def hasNext: Boolean = (q(x) + q(y) < 4) && iterationsRemaining >= 0

    def next(): Boolean = {
      val tx = q3(x) - 3 * x * q(y) + x0
      val ty = 3 * q(x) * y - q3(y) + y0
      y = ty
      x = tx
      iterationsRemaining -= 1
      hasNext
    }

    override def foldLeft[A](start: (X, Y) => A)(f: (A, X, Y) => A): A = {
      var v = start(x, y)
      while (next()) v = f(v, x, y)
      v
    }
    override def foldLeftX[A](start: X => A)(f: (A, X) => A): A = {
      var v = start(x)
      while (next()) v = f(v, x)
      v
    }

    override def foldLeftY[A](start: Y => A)(f: (A, Y) => A): A = {
      var v = start(y)
      while (next()) v = f(v, y)
      v
    }
  }

  def apply(maxIterations:Int):ContentFunction[Sequence] = (x0, y0) => new Sequence(x0, y0, maxIterations)
}
