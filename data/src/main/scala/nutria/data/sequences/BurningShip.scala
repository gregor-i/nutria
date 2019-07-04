package nutria.data.sequences

import nutria.core.Point
import nutria.data.DoubleSequence

object BurningShip {
  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var x, y = 0d
    private[this] var xx = x * x
    private[this] var yy = y * y
    def state = (x, y)

    def hasNext: Boolean = (xx + yy < 4) && iterationsRemaining >= 0

    def next(): (Double, Double) = {
      y = 2 * Math.abs(x * y) - y0
      x = xx - yy - x0
      xx = x * x
      yy = y * y
      iterationsRemaining -= 1
      (x, y)
    }
  }

  def apply(maxIterations: Int): Point => Sequence = p => new Sequence(p._1, p._2, maxIterations)
}
