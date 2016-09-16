package entities
package content

// Implements SSAA/FSAA
case class AntiAliasedFractalContent(fractal: Fractal, transform: Transform,
  accu: Accumulator, multi: Int)
  extends Content {

  val dimensions = transform.dimensions

  private val factor = 1.0 / multi

  def apply(x_i: Int, y_i: Int): Double =
    accu(
      for {
        x_s <- 0 until multi
        y_s <- 0 until multi
        x = transform.transformX(x_i, y_i, x_s, y_s, factor)
        y = transform.transformY(x_i, y_i, x_s, y_s, factor)
      } yield fractal(x, y)
    )
}

