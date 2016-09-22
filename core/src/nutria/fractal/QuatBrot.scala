package nutria
package fractal

import nutria.fractal.sequence.{HasSequenceConstructor, QuadBrotSequence}
import nutria.fractal.techniques.EscapeTechniques
import spire.math.Quaternion

class QuatBrot(selector: (Double, Double) => Quaternion[Double])
  extends HasSequenceConstructor[QuadBrotSequence]
  with EscapeTechniques[QuadBrotSequence] {

  override def sequence(x0: Double, y0: Double, maxIterations: Int): QuadBrotSequence = new QuadBrotSequence(selector(x0, y0), maxIterations)
}
