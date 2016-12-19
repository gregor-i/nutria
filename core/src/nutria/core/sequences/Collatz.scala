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
import spire.implicits._
import spire.math.Complex

object Collatz {

  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var c = Complex[Double](x0, y0)

    def publicX = c.real

    def publicY = c.imag

    def hasNext: Boolean = (c.imag.abs <= 2) && iterationsRemaining >= 0

    def next(): Boolean = {
      c = 0.25 * (2 + 7 * c - (Math.PI * c).cos * (2 +  5 * c))
      iterationsRemaining -= 1
      hasNext
    }

    override def foldLeft[A](start: (X, Y) => A)(f: (A, X, Y) => A): A = {
      var v = start(c.real, c.imag)
      while (next()) v = f(v, c.real, c.imag)
      v
    }
    override def foldLeftX[A](start: X => A)(f: (A, X) => A): A = {
      var v = start(c.real)
      while (next()) v = f(v, c.real)
      v
    }

    override def foldLeftY[A](start: Y => A)(f: (A, Y) => A): A = {
      var v = start(c.imag)
      while (next()) v = f(v, c.imag)
      v
    }
  }

  def apply(maxIterations:Int):ContentFunction[Sequence] = (x0, y0) => new Sequence(x0, y0, maxIterations)
}
