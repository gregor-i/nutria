package entities.fractal

class Tricorn(val maxIteration: Int) extends Fractal {

  override def apply(x0: Double, y0: Double): Double = {
    var x = x0
    var y = y0
    for (i <- 0 until maxIteration) {
      val xx = x * x
      val yy = y * y
      y = -2 * x * y + y0
      x = xx - yy + x0
      if (xx + yy > 4) return i / maxIteration.toDouble
    }
    1d
  }

  override def toString() = "Tricorn " + maxIteration
}
