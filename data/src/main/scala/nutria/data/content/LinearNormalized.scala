package nutria.data.content

object LinearNormalized {
  def apply(min: Double, max: Double): Double => Double =
    x => (x - min) / (max - min)

  def automatic(content: CachedContent[Double]): Content[Double] = {
    val values = content.values.flatten
    content.map(LinearNormalized(values.min, values.max))
  }
}
