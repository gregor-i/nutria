package nutria.fractal

import nutria.fractal.sequence.{BurningShipSequence, HasSequenceConstructor}
import nutria.fractal.technics.{EscapeTechnics, TrapTechnics}

object BurningShip
  extends HasSequenceConstructor[BurningShipSequence]
    with EscapeTechnics[BurningShipSequence]
    with TrapTechnics[BurningShipSequence] {

  val fractals = Seq(
    "RoughColoring(100)" -> RoughColoring(100),
    "RoughColoring(500)" -> RoughColoring(500),
    "RoughColoring(1000)" -> RoughColoring(1000)
  )

  override def sequence(x0: Double, y0: Double, maxIterations: Int): BurningShipSequence = new BurningShipSequence(x0, y0, maxIterations)
}
