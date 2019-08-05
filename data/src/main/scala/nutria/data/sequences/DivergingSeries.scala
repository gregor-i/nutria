package nutria.data.sequences

import nutria.core.Point
import nutria.data.DoubleSequence
import mathParser.implicits._
import nutria.core.languages.{Lambda, Z}
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
    val initial = series.initial.node
    val iteration = series.iteration.node

    p => new Sequence(Complex(p._1, p._2),
      series.maxIterations.value,
      series.escapeRadius.value * series.escapeRadius.value,
      lambda => nutria.core.languages.lambda.evaluate(initial)({case Lambda => lambda}),
      (z, lambda) => nutria.core.languages.zAndLambda.evaluate(iteration)({case Z => z; case Lambda => lambda})
    )
  }
}
