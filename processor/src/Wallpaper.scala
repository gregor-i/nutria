import nutria.accumulator.Max
import nutria.color.{Color, HSV}
import nutria.content.Content
import nutria.fractal.Mandelbrot
import nutria.syntax._
import nutria.viewport.{Dimensions, Viewport}
import viewportSelections.ViewportSelection

object Wallpaper extends ProcessorHelper {
  override def rootFolder: String = "/home/gregor/Pictures/Wallpaper/"

  override def statusPrints: Boolean = true

  case class WallpaperTask(view: Viewport, color: Color) extends Task {
    override def name: String = s"WallpaperTask($view)"

    override def skipCondition: Boolean = fileInRootFolder(s"$view/added.png").exists()

    override def execute(): Unit = {
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

    def main(args: Array[String]): Unit = {
      executeAllTasks(
        for (view <- ViewportSelection.selection)
          yield WallpaperTask(view, HSV.MonoColor.Blue))
    }
  }

}
