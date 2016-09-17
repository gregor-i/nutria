package entities.fractal

import entities.fractal.sequence.{HasSequenceConstructor, JuliaSetSequence}
import entities.fractal.technics.{CardioidTechnics, EscapeTechnics, TrapTechnics}

case class JuliaSet(cx: Double, cy: Double) extends HasSequenceConstructor[JuliaSetSequence]
  with EscapeTechnics[JuliaSetSequence]
  with TrapTechnics[JuliaSetSequence]
  with CardioidTechnics[JuliaSetSequence] {

  override def sequence(x0: Double, y0: Double, maxIterations: Int): JuliaSetSequence = new JuliaSetSequence(cx, cy)(x0, y0, maxIterations)
}
