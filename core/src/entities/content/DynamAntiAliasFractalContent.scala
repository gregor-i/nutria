package entities.content

import entities.accumulator._
import entities.fractal.technics.Fractal
import entities.viewport.Transform


case class DynamAntiAliasFractalContent(fractal: Fractal, transform: Transform,
                                        accumulator: Accumulator, limit: Double, minimalIterations: Int, maximalIterations: Int)
  extends Content {
  val dimensions = transform.dimensions

  val phi = (Math.sqrt(5) + 1) / 2
  val phi_loc = phi * width
  val phiphi_loc = phi * phi * height

  override def apply(x: Int, y: Int): Double = {
    val (x0, y0) = transform.transform(x, y)
    var (rx, ry) = (0.0, 0.0)
    var varianceState = Variance.neutral
    var accuState = accumulator.neutral
    var n = 0
    while (n <= minimalIterations || (n <= maximalIterations && Variance.lastOperation(varianceState, n) > limit * n)) {
      rx = rx + phi_loc
      ry = ry + phiphi_loc
      val f = fractal(x0 + rx % transform.scaleX + ry % transform.shearX,
                      y0 + rx % transform.shearY + ry % transform.scaleY)
      varianceState = Variance.fold(varianceState, f)
      accuState = accumulator.fold(accuState, f)
      n += 1
    }
    accumulator.lastOperation(accuState, n)
  }
}
