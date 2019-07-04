package nutria.data.content

import nutria.data.accumulator.Accumulator

case class AntiAliasedFractalContent(fractalContent: FunctionContent[Double], multi: Int, accu: Accumulator)
  extends Content[Double] {

  val dimensions = fractalContent.dimensions

  private val multiTransform = fractalContent.transform.copy(dimensions = dimensions.scale(multi))
  private val multiContent = fractalContent.copy(transform = multiTransform)

  def apply(x_i: Int, y_i: Int): Double =
    accu(for {
      x <- x_i * multi until (x_i + 1) * multi
      y <- y_i * multi until (y_i + 1) * multi
    } yield multiContent(x, y))
}

