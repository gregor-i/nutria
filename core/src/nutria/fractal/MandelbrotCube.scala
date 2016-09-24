package nutria.fractal

import nutria.fractal.techniques.{CardioidTechniques, ContourTechniques, EscapeTechniques, TrapTechniques}

object MandelbrotCube {


  final class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence { self =>
    private[this] var x: X = 0d
    private[this] var y: Y = 0d
    private[this] var xx = x * x
    private[this] var yy = y * y
    def publicX = x
    def publicY = y

    @inline def hasNext: Boolean = (x*x + y*y < 4) && iterationsRemaining >= 0

    @inline def next(): Boolean = {
      val ty = -3 * y * y * x + x * x * x + y0
      val tx = -y * y * y + 3 * y * x * x + x0
      y = ty
      x = tx
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

//  val fractals = Seq(
//    "RoughColoring(100)" -> RoughColoring(100),
//    "RoughColoring(500)" -> RoughColoring(500),
//    "RoughColoring(1000)" -> RoughColoring(1000)
//  )


  implicit val seqConstructor = new SequenceConstructor[Sequence] {
    override def apply(x0: Double, y0: Double, maxIterations: Int): Sequence = new Sequence(x0, y0, maxIterations)
  }
}
