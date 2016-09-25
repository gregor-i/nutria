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

case class JuliaSet(cx: Double, cy: Double) {

  class Sequence(cx: Double, cy: Double)(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var x: X = x0
    private[this] var y: Y = y0
    private[this] var xx = x * x
    private[this] var yy = y * y

    def publicX = x

    def publicY = y

    @inline def hasNext: Boolean = (xx + yy < 4) && iterationsRemaining >= 0

    @inline override def next(): Boolean = {
      y = 2 * x * y + cy
      x = xx - yy + cx
      xx = x * x
      yy = y * y
      iterationsRemaining -= 1
      hasNext
    }

    @inline override def foldLeft(start: Double)(@inline f: (Double, X, Y) => Double): Double = {
      var v = start
      while (next()) v = f(v, x, y)
      v
    }

    @inline override def foldLeftX(start: Double)(@inline f: (Double, X) => Double): Double = {
      var v = start
      while (next()) v = f(v, x)
      v
    }

    @inline override def foldLeftY(start: Double)(@inline f: (Double, Y) => Double): Double = {
      var v = start
      while (next()) v = f(v, y)
      v
    }
  }


  //  val fractals = Seq(
  //    "RoughColoring(100)" -> RoughColoring(100),
  //    "RoughColoring(500)" -> RoughColoring(500),
  //    "RoughColoring(1000)" -> RoughColoring(1000)
  //  )

  implicit val seqConstructor = new SequenceConstructor[Sequence] {
    override def apply(x0: Double, y0: Double, maxIterations: Int): Sequence = new Sequence(cx, cy)(x0, y0, maxIterations)
  }
}
