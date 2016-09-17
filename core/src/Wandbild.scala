import entities.accumulator.Max
import entities.color.{Color, HSV}
import entities.content.Content
import entities.fractal.Mandelbrot
import entities.syntax._
import entities.viewport.{Dimensions, Viewport}

object Wandbild {
  def make(path: String, view: Viewport)(color: Color): Unit = {
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

    rough.withColor(color).save(s"$path\\rough.png")
    circle.withColor(color).save(s"$path\\circle.png")
    added.withColor(color).save(s"$path\\added.png")
  }

  def main(args: Array[String]) = {
    for (view <- Viewport.auswahl) {
      make(s"auswahl\\$view", view)(HSV.MonoColor.Blue)
    }
  }
}
