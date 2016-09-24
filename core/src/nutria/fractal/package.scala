package nutria

import simulacrum._

package object fractal {

  @typeclass trait SequenceConstructor[A <: AbstractSequence] {
    def apply(x0: Double, y0: Double, maxIterations: Int): A
  }

}
