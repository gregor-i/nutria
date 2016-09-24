package nutria
package fractal

import nutria.fractal.techniques.{EscapeTechniques, TrapTechniques}
import spire.math.Quaternion
import spire.implicits._

class QuaternionBrot(selector: (Double, Double) => Quaternion[Double]) {

  final class Sequence(val start: Quaternion[Double], private var iterationsRemaining: Int) extends AbstractSequence {
    var current: Quaternion[Double] = Quaternion.zero

    @inline def hasNext: Boolean = current.abs < 2 && iterationsRemaining >= 0

    @inline def next(): Boolean = {
      current = current * current + start
      iterationsRemaining -= 1
      hasNext
    }
  }

  implicit val seqConstructor = new SequenceConstructor[Sequence] {
    override def apply(x0: Double, y0: Double, maxIterations: Int): Sequence = new Sequence(selector(x0, y0), maxIterations)
  }
}
