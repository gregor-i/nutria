import entities.accumulator.Max
import entities.color.{Color, HSV}
import entities.content.Content
import entities.fractal.Mandelbrot
import entities.syntax._
import entities.viewport.{Dimensions, Viewport}
import viewportSelections.ViewportSelection

object Wallpaper extends ProcessorHelper {
  override def rootFolder: String = "/home/gregor/Pictures/Wallpaper/"

  override def statusPrints: Boolean = true

  def make(view: Viewport, color: Color): Unit = {
    val transform = view
      .withDimensions(Dimensions.fullHD)

    val rough = transform
      .withAntiAliasedFractal(Mandelbrot.RoughColoring(5000)).strongNormalized

    val circle = transform
      .withAntiAliasedFractal(Mandelbrot.CircleP2(7500), Max).strongNormalized

    val added = new Content {
      override def dimensions: Dimensions = Dimensions.fullHD

      override def apply(x: Int, y: Int): Double = rough(x, y) + circle(x, y)
    }.strongNormalized

    rough.withColor(color).save(fileInRootFolder(s"$view/rough.png"))
    circle.withColor(color).save(fileInRootFolder(s"$view/circle.png"))
    added.withColor(color).save(fileInRootFolder(s"$view/added.png"))
  }

  def main(args: Array[String]):Unit = {
    makeAll(
      (for (view <- ViewportSelection.selection)
        yield () => make(view, HSV.MonoColor.Blue)).toSeq
    )

  }
}
