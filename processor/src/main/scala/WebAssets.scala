import image.Image
import nutria.core.Color
import nutria.core.accumulator.Arithmetic
import nutria.core.colors.RGB
import nutria.core.syntax._
import nutria.core.viewport.Dimensions
import nutria.data.Defaults
import nutria.data.consumers.CountIterations
import nutria.data.sequences.Mandelbrot

object WebAssets extends App with Defaults {
  val color = new Color[Double] {
    override def apply(v1: Double): RGB = RGB.interpolate(RGB(238d, 238d, 238d), RGB.black, v1)
  }


  Image.verboseSave(
    defaultViewport
      .cover(57, 32)
      .withDimensions(Dimensions(57, 32).scale(4))
      .withContent(Mandelbrot(350, 100d) andThen CountIterations.double())
      .multisampled(Arithmetic, 4)
      .cached
      .linearNormalized
      .withColor(color),
    DefaultSaveFolder.defaultSaveFolder / "service" / "public" / "img" /~ "icon.png")
}
