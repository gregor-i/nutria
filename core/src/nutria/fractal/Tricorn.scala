package nutria.fractal

import nutria.fractal.sequence.{HasSequenceConstructor, TricornSequence}
import nutria.fractal.techniques.{EscapeTechniques, TrapTechniques}

object Tricorn
  extends HasSequenceConstructor[TricornSequence]
    with EscapeTechniques[TricornSequence]
    with TrapTechniques[TricornSequence] {

  val fractals = Seq(
    "RoughColoring(100)" -> RoughColoring(100),
    "RoughColoring(500)" -> RoughColoring(500),
    "RoughColoring(1000)" -> RoughColoring(1000)
  )

  override def sequence(x0: Double, y0: Double, maxIterations: Int): TricornSequence = new TricornSequence(x0, y0, maxIterations)
}
