package entities.fractal

import entities.fractal.sequence.{HasSequenceConstructor, TricornSequence}
import entities.fractal.technics.{CardioidTechnics, EscapeTechnics, TrapTechnics}

object Tricorn extends HasSequenceConstructor[TricornSequence]
  with EscapeTechnics[TricornSequence]
  with TrapTechnics[TricornSequence]
  with CardioidTechnics[TricornSequence] {

  override def sequence(x0: Double, y0: Double, maxIterations: Int): TricornSequence = new TricornSequence(x0, y0, maxIterations)
}
