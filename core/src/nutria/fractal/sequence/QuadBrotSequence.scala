package nutria.fractal.sequence

import spire.math.Quaternion
import spire.implicits._


final class QuadBrotSequence(val start: Quaternion[Double], private var iterationsRemaining: Int) extends Sequence { self =>
  var current: Quaternion[Double] = Quaternion.zero

  @inline def hasNext: Boolean = current.abs < 2 && iterationsRemaining >= 0

  @inline def next() : Boolean = {
    current = current * current + start
    iterationsRemaining -= 1
    hasNext
  }
}