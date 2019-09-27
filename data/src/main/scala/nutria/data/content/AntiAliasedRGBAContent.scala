package nutria.data.content

import nutria.core.RGBA

case class AntiAliasedRGBAContent(fractalContent: FunctionContent[RGBA], multi: Int)
  extends Content[RGBA] {

  val dimensions = fractalContent.dimensions

  private val multiSquard = multi * multi

  private val multiTransform = fractalContent.transform.copy(dimensions = dimensions.scale(multi))
  private val multiContent = fractalContent.copy(transform = multiTransform)

  private def multisampled(it: Iterable[RGBA]) = {
    var sumR = 0d
    var sumG = 0d
    var sumB = 0d
    var sumA = 0d
    it.foreach { rgba =>
      sumR += rgba.R
      sumG += rgba.G
      sumB += rgba.B
      sumA += rgba.A
    }
    RGBA(sumR / multiSquard, sumG / multiSquard, sumB / multiSquard, sumA / multiSquard)
  }

  def apply(x_i: Int, y_i: Int): RGBA =
    multisampled(for {
      x <- x_i * multi until (x_i + 1) * multi
      y <- y_i * multi until (y_i + 1) * multi
    } yield multiContent(x, y))
}

