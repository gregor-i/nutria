package nutria.data.sequences

import nutria.core.Point
import nutria.data.DoubleSequence
import nutria.core.divergingSeries.Language._
import mathParser.implicits._
import nutria.core.divergingSeries.{Lambda, Z}
import spire.math.Complex

object DivergingSeries {
  final class Sequence(lambda: Complex[Double],
                       private var iterationsRemaining: Int,
                       escapeOrbitSquared: Double,
                       initial: Complex[Double] => Complex[Double],
                       iteration: (Complex[Double], Complex[Double]) => Complex[Double]) extends DoubleSequence {
    private var z: Complex[Double] = initial(lambda)

    def state = z.asTuple

    def hasNext: Boolean = (z.absSquare < escapeOrbitSquared) && iterationsRemaining >= 1

    def next(): (Double, Double) = {
      z = iteration(z, lambda)
      iterationsRemaining -= 1
      z.asTuple
    }
  }

  def apply(series: nutria.core.DivergingSeries): Point => Sequence = {
    val initial = c0Lang.optimize(c0Lang.parse(series.initial).get)
    val iteration = fLang.optimize(fLang.parse(series.iteration).get)

    p => new Sequence(Complex(p._1, p._2),
      series.maxIterations,
      series.escapeRadius * series.escapeRadius,
      lambda => c0Lang.evaluate(initial)({case Lambda => lambda}),
      (z, lambda) => fLang.evaluate(iteration)({case Z => z; case Lambda => lambda})
    )
  }
}
