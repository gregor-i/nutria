package nutria.fractal.alternativeImplementions

import nutria.fractal.sequence.{HasSequenceConstructor, Sequence}
import nutria.fractal.technics.EscapeTechnics
import spire.implicits._
import spire.math.Complex

final class SpireBrotSequence(val start: Complex[Double], private var iterationsRemaining: Int) extends Sequence {
  private var current: Complex[Double] = Complex.zero

  @inline def hasNext: Boolean = current.abs < 2 && iterationsRemaining >= 0

  @inline def next(): Boolean = {
    current = current * current + start
    iterationsRemaining -= 1
    hasNext
  }
}

object SpireBrot
  extends HasSequenceConstructor[SpireBrotSequence]
    with EscapeTechnics[SpireBrotSequence] {

  override def sequence(x0: Double, y0: Double, maxIterations: Int): SpireBrotSequence = new SpireBrotSequence(Complex(x0, y0), maxIterations)
}
