package nutria.fractal

trait AbstractSequence {
  @inline def hasNext: Boolean

  @inline def next(): Boolean

  @inline def size(): Int = {
    var i = 0
    while (next()) i = i + 1
    i
  }
}

trait DoubleSequence extends AbstractSequence { self =>
  type X = Double
  type Y = Double

  def publicX: X
  def publicY: X
  def public: (X, Y) = (publicX, publicY)

  @inline def foldLeft(start: Double)(@inline f: (Double, X, Y) => Double): Double

  @inline def foldLeftX(start: Double)(@inline f: (Double, X) => Double): Double

  @inline def foldLeftY(start: Double)(@inline f: (Double, Y) => Double): Double

  def wrapped: scala.Iterator[(X, Y)] =
    new scala.Iterator[(X, Y)] {
      override def hasNext: Boolean = self.hasNext

      override def next(): (X, Y) = {
        self.next()
        (publicX, publicY)
      }
    }
}