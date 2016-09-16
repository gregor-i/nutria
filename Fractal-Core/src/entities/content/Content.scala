package entities
package content

import entities.viewport.HasDimensions

trait Content extends HasDimensions {
  def apply(x: Int, y: Int): Double
}

object CachedContent{
  def cache(content: Content): Seq[Seq[Double]] =
    (0 until content.width).par.map(x => (0 until content.height).map(y => content(x, y))).seq
}

class CachedContent(val values: Seq[Seq[Double]], val dimensions: Dimensions) extends Content {
  def this(content: Content) = this(CachedContent.cache(content), content.dimensions)

  override def apply(x: Int, y: Int): Double = values(x)(y)

  def applyAccumulator(accu: Accumulator): Double =
    accu(values.flatten)
}
