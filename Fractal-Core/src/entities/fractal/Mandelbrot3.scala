package entities.fractal

class Mandelbrot3(val maxIteration: Int) extends Fractal {

  override def apply(x0: Double, y0: Double): Double = {
    var x = x0
    var y = y0
    for (i <- 0 until maxIteration) {
      val ty = -3 * y * y * x + x * x * x + y0
      val tx = -y * y * y + 3 * y * x * x + x0
      y = tx
      x = ty
      if (x * x + y * y > 4) return i / maxIteration.toDouble
    }
    1d
  }

  override def toString() = "Mandelbrot3 " + maxIteration
}
