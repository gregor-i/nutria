package nutria
package fractal

import nutria.fractal.sequence.{HasSequenceConstructor, MandelbrotSequence}
import nutria.fractal.technics.{CardioidTechnics, EscapeTechnics, TrapTechnics}
import nutria.syntax.FoldBoooleans
import nutria.viewport.Point

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
    "Contour(500)" -> Contour(500),
    "CardioidHeuristic(50, 20)" -> CardioidHeuristic(50, 20),
    "CardioidNumeric(50, 20)" -> CardioidNumeric(50, 20))

  override def sequence(x0: Double, y0: Double, maxIterations: Int): MandelbrotSequence = new MandelbrotSequence(x0, y0, maxIterations)

  // Careful. A lot of strage double magic goes on in this function. ContourCompare is an implementation of the same function to compare.
  def Contour(maxIterations: Int): Fractal =
  (x0, y0) => {
    val seq = sequence(x0, y0, maxIterations)
    var distance = 0d
    for (i <- 0 to maxIterations) {
      seq.next()
      if (seq.x.abs > distance)
        distance = seq.x.abs
    }
    (distance == Double.PositiveInfinity).fold(0, 1)
  }

  private def ContourCompare(maxIterations: Int): Fractal =
    (x0, y0) => {
      var distance = 10d
      var x = x0
      var y = y0
      for (i <- 0 until maxIterations) {
        val xx = x * x
        val yy = y * y
        y = 2 * x * y + y0
        x = xx - yy + x0

        if (x.abs > distance)
          distance = x.abs
      }
      if (distance == Double.PositiveInfinity)
        1d
      else
        0d
    }
}