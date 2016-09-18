package nutria.fractal

import nutria.fractal.sequence.{HasSequenceConstructor, MandelbrotCubeSequence}
import nutria.fractal.technics.{EscapeTechnics, TrapTechnics}

object MandelbrotCube
  extends HasSequenceConstructor[MandelbrotCubeSequence]
    with EscapeTechnics[MandelbrotCubeSequence]
    with TrapTechnics[MandelbrotCubeSequence] {

  val fractals = Seq(
    "RoughColoring(100)" -> RoughColoring(100),
    "RoughColoring(500)" -> RoughColoring(500),
    "RoughColoring(1000)" -> RoughColoring(1000)
  )

  override def sequence(x0: Double, y0: Double, maxIterations: Int): MandelbrotCubeSequence = new MandelbrotCubeSequence(x0, y0, maxIterations)
}
