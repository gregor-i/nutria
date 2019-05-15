package nutria.data.sequences

import nutria.core.{DoubleSequence, Point}

class JuliaSet(cx: Double, cy: Double) {

  class Sequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence {
    private[this] var x = x0
    private[this] var y = y0
    private[this] var xx = x * x
    private[this] var yy = y * y

    def state = (x, y)

    def hasNext: Boolean = (xx + yy < 4) && iterationsRemaining >= 0

    override def next(): (Double, Double) = {
      y = 2 * x * y + cy
      x = xx - yy + cx
      xx = x * x
      yy = y * y
      iterationsRemaining -= 1
      (x, y)
    }
  }

  def apply(maxIterations:Int):Point => Sequence = p => new Sequence(p._1, p._2, maxIterations)
}
