package entities.fractal.sequence

final class BurningShipSequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends Sequence2[Double, Double] { self =>
  var x:X = 0d
  var y:Y = 0d
  private var xx = x * x
  private var yy = y * y

  @inline def hasNext: Boolean = (xx + yy < 4) && iterationsRemaining >= 0

  @inline def next(): Boolean = {
    y = 2 * Math.abs(x * y) - y0
    x = xx - yy - x0
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