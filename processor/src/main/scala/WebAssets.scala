
import nutria.core.viewport.Dimensions
import nutria.data.accumulator.Arithmetic
import nutria.data.colors.RGB
import nutria.data.consumers.CountIterations
import nutria.data.image.Image
import nutria.data.sequences.Mandelbrot
import nutria.data.syntax._
import nutria.data.{Color, Defaults}

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
    defaultSaveFolder / "service" / "public" / "img" /~ "icon.png")
}
