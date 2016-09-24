package nutria
package fractal

import nutria.fractal.techniques.{CardioidTechniques, ContourTechniques, EscapeTechniques, TrapTechniques}
import nutria.viewport.Point

object Mandelbrot {

  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var x: X = 0d
    private[this] var y: Y = 0d
    private[this] var xx = x * x
    private[this] var yy = y * y

    def publicX = x

    def publicY = y

    private[this] var t = 0d

    @inline def hasNext: Boolean = (xx + yy < 4) && iterationsRemaining >= 0

    @inline def next(): Boolean = {
      t += 1
      y = 2 * x * y + y0
      x = xx - yy + x0
      xx = x * x
      yy = y * y
      iterationsRemaining -= 1
      hasNext
    }

    @inline override def foldLeft(start: Double)(@inline f: (Double, X, Y) => Double): Double = {
      var v = start
      while (next()) v = f(v, x, y)
      v
    }

    @inline override def foldLeftX(start: Double)(@inline f: (Double, X) => Double): Double = {
      var v = start
      while (next()) v = f(v, x)
      v
    }

    @inline override def foldLeftY(start: Double)(@inline f: (Double, Y) => Double): Double = {
      var v = start
      while (next()) v = f(v, y)
      v
    }
  }

  val start: Viewport = Viewport(Point(-2.5, -1), Point(3.5, 0), Point(0, 2))

  implicit val seqConstructor = new SequenceConstructor[Sequence] {
    override def apply(x0: Double, y0: Double, maxIterations: Int): Sequence = new Sequence(x0, y0, maxIterations)
  }

}