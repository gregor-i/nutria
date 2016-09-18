package nutria.fractal.sequence

trait Sequence {
  @inline def hasNext: Boolean

  @inline def next(): Boolean

  @inline def size(): Int = {
    var i = 0
    while (next()) i = i + 1
    i
  }
}

trait Sequence2[_X, _Y] extends Sequence { self =>
  type X = _X
  type Y = _Y
  @inline def x: X

  @inline def y: Y

  @inline def foldLeft(start: Double)(@inline f: (Double, X, Y) => Double): Double

  @inline def foldLeftX(start: Double)(@inline f: (Double, X) => Double): Double

  @inline def foldLeftY(start: Double)(@inline f: (Double, Y) => Double): Double

  def wrapped: scala.Iterator[(X, Y)] =
    new scala.Iterator[(X, Y)] {
      override def hasNext: Boolean = self.hasNext

      override def next(): (X, Y) = {
        self.next()
        (x, y)
      }
    }
}