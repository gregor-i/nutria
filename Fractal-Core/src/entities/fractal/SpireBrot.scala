package entities
package fractal

import spire.math.Complex
import spire.implicits._

object SpireBrot {

  final class Iterator(val start: Complex[Double], private var iterationsRemaining: Int) { self =>
    private var current: Complex[Double] = Complex.zero

    @inline def hasNext: Boolean = current.abs < 2 && iterationsRemaining >= 0

    @inline def next(): Boolean = {
      current = current * current + start
      iterationsRemaining -= 1
      hasNext
    }

    @inline def size(): Int = {
      var i = 0
      while (next()) i = i + 1
      i
    }
  }

  case class RoughColoring(maxIteration: Int) extends Fractal {
    override def apply(x0: Double, y0: Double): Double =
      new Iterator(Complex(x0, y0), maxIteration).size()
  }
}
