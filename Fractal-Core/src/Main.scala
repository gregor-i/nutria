import entities.color.HSV
import entities.fractal.Mandelbrot
import entities.syntax._
import entities.viewport.{Dimensions, ViewportUtil}


object Main extends App {

  val root = "/home/gregor/Pictures/Wallpapers/"
  ViewportUtil.start
    .withDimensions(Dimensions.fujitsu)
    .withDynamAntiAliasedFractal(Mandelbrot.RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"dynam.png")

  ViewportUtil.start
    .withDimensions(Dimensions.fujitsu)
    .withAntiAliasedFractal(Mandelbrot.RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"aa.png")


  ViewportUtil.start
    .withDimensions(Dimensions.fujitsu)
    .withFractal(Mandelbrot.RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"basic.png")


}
