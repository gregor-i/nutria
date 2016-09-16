package entities.content
import entities.viewport.Dimensions

object Spectrum extends Content with Normalized {
  override def apply(x: Int, y: Int): Double = x.toDouble / Dimensions.screenHD.width

  override def dimensions: Dimensions = Dimensions.screenHD
}
