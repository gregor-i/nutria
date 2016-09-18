package entities
package fractal

import entities.fractal.sequence.{HasSequenceConstructor, QuadBrotSequence}
import entities.fractal.technics.EscapeTechnics
import spire.math.Quaternion

class QuatBrot(selector: (Double, Double) => Quaternion[Double])
  extends HasSequenceConstructor[QuadBrotSequence]
  with EscapeTechnics[QuadBrotSequence] {

  override def sequence(x0: Double, y0: Double, maxIterations: Int): QuadBrotSequence = new QuadBrotSequence(selector(x0, y0), maxIterations)
}
