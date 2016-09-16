package entities
package content

import entities.accumulator.{Max, Min}

trait Normalized

case class LinearNormalizedContent(content: CachedContent) extends Content with Normalized {
  val dimensions = content.dimensions

  private val max = content.applyAccumulator(Max)
  private val min = content.applyAccumulator(Min)
  require(max != min)
  private val dy: Double = 1.0 / (max - min)
  private val y0: Double = -min / (max - min)

  @inline private def clamp(v: Double, max: Double, min: Double) =
    if (v > max) max
    else if (v < min) min
    else v

  def apply(x: Int, y: Int): Double = {
    clamp(y0 + content(x, y) * dy, 1, 0)
  }
}

case class StrongNormalizedContent(content: CachedContent) extends Content with Normalized {
  val dimensions = content.dimensions

  private val map = (for (x <- 0 until width; y <- 0 until height)
    yield content(x, y) -> (x, y)).groupBy(_._1).mapValues(_.map(_._2))

  private val sorted = map.toSeq.sortBy(_._1)

  private val values = Array.fill[Double](width, height)(0d)

  private var finished = 0
  
  for ((value, pos) <- sorted) {
    for ((x, y) <- pos) {
      values(x)(y) = finished.toDouble / (width * height)
    }
    finished += pos.size
  }

  override def apply(x: Int, y: Int): Double = values(x)(y)
}
