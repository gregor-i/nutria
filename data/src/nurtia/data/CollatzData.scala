package nurtia.data

import nutria.fractal.Collatz
import nutria.fractal.techniques.{EscapeTechniques, TrapTechniques}
import nutria.viewport.Point
import nutria.{Fractal, Viewport}

object CollatzData extends Data[Collatz.Sequence] {

  import Collatz.Sequence

  val initialViewport: Viewport = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))

  val selectionViewports: Set[Viewport] = Set.empty

  val selectionFractals: Seq[(String, Fractal)] = Seq(
    "RoughColoring(50)" -> EscapeTechniques[Sequence].RoughColoring(50),
    "OrbitPoint(50, 0, 0)" -> TrapTechniques[Sequence].OrbitPoint(50, 0, 0))
}

