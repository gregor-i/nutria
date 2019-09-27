
import nutria.core.RGBA
import nutria.core.viewport.{Dimensions, Viewport}
import nutria.data.accumulator.Arithmetic
import nutria.data.consumers.CountIterations
import nutria.data.image.Image
import nutria.data.sequences.Mandelbrot
import nutria.data.syntax._
import nutria.data.{Color, Defaults}

object WebAssets extends App with Defaults {
  val color = new Color[Double] {
    override def apply(v1: Double): RGBA = RGBA.black.copy(A = v1)
  }

  val viewport = Viewport(
    origin = (-1.59,-1.05),
    A = (0.0,2.1),
    B = (2.1,0.0)
  )

  val img = viewport
    .withDimensions(Dimensions(64, 64).scale(4))
    .withContent(Mandelbrot(350, 100d) andThen CountIterations.double())
    .multisampled(Arithmetic, 4)
    .cached
    .linearNormalized
    .withColor(color)
    .cached

  Image.verboseSave(img, RGBA.white, defaultSaveFolder / "service" / "public" / "img" /~ "icon.png")
}
