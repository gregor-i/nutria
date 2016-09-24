package nutria.fractal.techniques

import nutria._
import nutria.fractal.{DoubleSequence, SequenceConstructor}
import nutria.syntax._

object ContourTechniques{
  def apply[A <: DoubleSequence:SequenceConstructor]:ContourTechniques[A] = new ContourTechniques[A]
}

class ContourTechniques[A <: DoubleSequence](implicit sequence:SequenceConstructor[A]) {
  // Careful. A lot of strage double magic goes on in this function. ContourCompare is an implementation of the same function to compare for the Mandelbrot sequence.
  def Contour(maxIterations: Int): Fractal =
  (x0, y0) => {
    val seq = sequence(x0, y0, maxIterations)
    var distance = 0d
    for (i <- 0 to maxIterations) {
      seq.next()
      if (seq.publicX.abs > distance)
        distance = seq.publicX.abs
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
