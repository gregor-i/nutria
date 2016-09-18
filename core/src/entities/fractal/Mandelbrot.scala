package entities
package fractal

import entities.fractal.sequence.{HasSequenceConstructor, MandelbrotSequence, Sequence, Sequence2}
import entities.fractal.technics.{CardioidTechnics, EscapeTechnics, TrapTechnics}
import entities.viewport.Point


object Mandelbrot
  extends HasSequenceConstructor[MandelbrotSequence]
    with EscapeTechnics[MandelbrotSequence]
    with TrapTechnics[MandelbrotSequence]
    with CardioidTechnics[MandelbrotSequence] {

  val start = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))

  val fractals = Seq(
    "RoughColoring(150)" -> RoughColoring(150),
    "RoughColoring(250)" -> RoughColoring(250),
    "RoughColoring(500)" -> RoughColoring(500),
    "RoughColoring(750)" -> RoughColoring(750),
    "RoughColoring(1500)" -> RoughColoring(1500),
    "OrbitPoint(250, 0, 0)" -> OrbitPoint(250, 0, 0),
    "OrbitPoint(250, -1, 0)" -> OrbitPoint(250, -1, 0),
    "OrbitPoint(1250, 1, 1)" -> OrbitPoint(1250, 1, 1),
    "OrbitPoint(250, 1, 0)" -> OrbitPoint(250, 1, 0),
    "OrbitPoint(250, 0, 1)" -> OrbitPoint(250, 0, 1),
    "OrbitRealAxis(250)" -> OrbitRealAxis(250),
    "OrbitImgAxis(250)" -> OrbitImgAxis(250),
    "Brot(1000)" -> Brot(1000),
    "CardioidHeuristic(50, 20)" -> CardioidHeuristic(50, 20),
    "CardioidNumeric(50, 20)" -> CardioidNumeric(50, 20))

  override def sequence(x0: Double, y0: Double, maxIterations: Int): MandelbrotSequence = new MandelbrotSequence(x0, y0, maxIterations)
}