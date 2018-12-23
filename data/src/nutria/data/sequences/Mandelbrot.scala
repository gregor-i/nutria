package nutria.data.sequences

import nutria.core.{DoubleSequence, MathUtils, Point}

object Mandelbrot extends MathUtils {
  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int, escapeOrbitSquared:Double) extends DoubleSequence {
    private[this] var x, y = 0d
    private[this] var xx = x * x
    private[this] var yy = y * y

    def state = (x, y)

    def hasNext: Boolean = (xx + yy < escapeOrbitSquared) && iterationsRemaining >= 1

    def next(): (Double, Double) = {
      y = 2 * x * y + y0
      x = xx - yy + x0
      xx = q(x)
      yy = q(y)
      iterationsRemaining -= 1
      (x, y)
    }
  }

  def apply(maxIterations:Int, escapeOrbit:Double = 2):Point => Sequence = p => new Sequence(p._1, p._2, maxIterations, escapeOrbit*escapeOrbit)
}
