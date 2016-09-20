package nutria.fractal.sequence


final class MandelbrotSequence(x0: Double, y0: Double, private var iterationsRemaining: Int) extends DoubleSequence { self =>
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

final class MandelbrotSequence2(x0: Double, y0: Double, private var iterationsRemaining: Int) extends Sequence {
  type X = Double
  type Y = Double
  private[this] var x:X = 0d
  private[this] var y:Y = 0d
  private[this] var xx = x * x
  private[this] var yy = y * y

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

  @inline  def foldLeft(start: Double)(@inline f: (Double, X, Y) => Double): Double = {
    var v = start
    while (next()) v = f(v, x, y)
    v
  }

  @inline  def foldLeftX(start: Double)(@inline f: (Double, X) => Double): Double = {
    var v = start
    while (next()) v = f(v, x)
    v
  }

  @inline  def foldLeftY(start: Double)(@inline f: (Double, Y) => Double): Double = {
    var v = start
    while (next()) v = f(v, y)
    v
  }
}