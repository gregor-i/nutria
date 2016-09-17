package entities.content
import entities.viewport.Dimensions

object Spectrum extends Content with Normalized {
  override def apply(x: Int, y: Int): Double = x.toDouble / Dimensions.fujitsu.width

  override def dimensions: Dimensions = Dimensions.fujitsu
}
