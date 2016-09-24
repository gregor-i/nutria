package nutria.fractal.alternativeImplementions

import nutria.fractal.{AbstractSequence, SequenceConstructor}
import spire.implicits._
import spire.math.Complex


object SpireBrot {

  final class Sequence(val start: Complex[Double], private var iterationsRemaining: Int) extends AbstractSequence {
    private var current: Complex[Double] = Complex.zero

    @inline def hasNext: Boolean = current.abs < 2 && iterationsRemaining >= 0

    @inline def next(): Boolean = {
      current = current * current + start
      iterationsRemaining -= 1
      hasNext
    }
  }

  implicit val seqConstructor = new SequenceConstructor[Sequence] {
    override def apply(x0: Double, y0: Double, maxIterations: Int): Sequence = new Sequence(Complex(x0, y0), maxIterations)
  }
}
