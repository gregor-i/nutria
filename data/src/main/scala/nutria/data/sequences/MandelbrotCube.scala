package nutria.data.sequences

import nutria.core.Point
import nutria.data.{DoubleSequence, MathUtils}

object MandelbrotCube extends MathUtils {

  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var x, y = 0d

    def state = (x, y)

    def hasNext: Boolean = (q(x) + q(y) < 4) && iterationsRemaining >= 0

    def next(): (Double, Double) = {
      val tx = q3(x) - 3 * x * q(y) + x0
      val ty = 3 * q(x) * y - q3(y) + y0
      y = ty
      x = tx
      iterationsRemaining -= 1
      (x, y)
    }
  }

  def apply(maxIterations: Int): Point => Sequence = p => new Sequence(p._1, p._2, maxIterations)
}
