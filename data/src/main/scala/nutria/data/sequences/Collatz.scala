package nutria.data.sequences

import nutria.core.Point
import nutria.data.DoubleSequence
import spire.implicits._
import spire.math.Complex

object Collatz {

  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var c = Complex[Double](x0, y0)

    def state = c.asTuple

    def hasNext: Boolean = (c.imag.abs <= 2) && iterationsRemaining >= 0

    def next(): (Double, Double) = {
      c = 0.25 * (2 + 7 * c - (Math.PI * c).cos * (2 + 5 * c))
      iterationsRemaining -= 1
      c.asTuple
    }
  }

  def apply(maxIterations: Int): Point => Sequence = p => new Sequence(p._1, p._2, maxIterations)
}
