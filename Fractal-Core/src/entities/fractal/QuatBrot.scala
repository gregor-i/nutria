package entities
package fractal

import spire.math.Quaternion
import spire.implicits._


object QuatBrot {

  final class Iterator(val start: Quaternion[Double], private var iterationsRemaining: Int) { self =>
    var current: Quaternion[Double] = Quaternion.zero

    @inline def hasNext: Boolean = current.abs < 2 && iterationsRemaining >= 0

    @inline def step(): Unit = {
      current = current * current + start
      iterationsRemaining -= 1
    }

    @inline def stepAndHasNext(): Boolean = { step(); hasNext }

    @inline def size(): Int = {
      var i = 0
      while (stepAndHasNext()) i += 1
      i
    }
  }

  case class RoughColoring(maxIteration: Int)(selector: (Double, Double) => Quaternion[Double]) extends Fractal {
    override def apply(x0: Double, y0: Double): Double =
      new Iterator(selector(x0, y0), maxIteration).size()
  }
}
