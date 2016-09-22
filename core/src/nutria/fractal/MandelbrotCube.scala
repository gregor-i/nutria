package nutria.fractal

import nutria.fractal.sequence.{HasSequenceConstructor, MandelbrotCubeSequence}
import nutria.fractal.techniques.{EscapeTechniques, TrapTechniques}

object MandelbrotCube
  extends HasSequenceConstructor[MandelbrotCubeSequence]
    with EscapeTechniques[MandelbrotCubeSequence]
    with TrapTechniques[MandelbrotCubeSequence] {

  val fractals = Seq(
    "RoughColoring(100)" -> RoughColoring(100),
    "RoughColoring(500)" -> RoughColoring(500),
    "RoughColoring(1000)" -> RoughColoring(1000)
  )

  override def sequence(x0: Double, y0: Double, maxIterations: Int): MandelbrotCubeSequence = new MandelbrotCubeSequence(x0, y0, maxIterations)
}
