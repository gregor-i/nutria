package nutria.core.content

object HistogramNormalized {
  private def helper[A: Ordering](content: CachedContent[A]): Seq[Seq[Double]] = {
    val width = content.width
    val height = content.height

    val swappedAndSorted = (for {
      x <- 0 until width
      y <- 0 until height
    } yield content(x, y) -> (x, y)).toList.sortBy(_._1)

    val values = Array.fill[Double](width, height)(0d)

    var list = swappedAndSorted
    var finished = 0
    while (list.nonEmpty) {
      val headKey = list.head._1
      val (part, remaining) = list.span(_._1 == headKey)
      val partSize = part.size

      val value = (finished + partSize / 2d) / (width * height)
      part.foreach { case (_, (x, y)) => values(x)(y) = value }

      list = remaining
      finished += partSize
    }
    assert(list == Nil)
    assert(finished == width * height)

    values.map(_.toSeq)
  }
}

case class HistogramNormalized[A: Ordering](content: CachedContent[A])
  extends CachedContent[Double](HistogramNormalized.helper(content), content.dimensions)
