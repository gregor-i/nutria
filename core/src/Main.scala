import entities.color.HSV
import entities.fractal.Mandelbrot
import entities.syntax._
import entities.viewport.Dimensions


object Main extends App {

  val root = "/home/gregor/Pictures/Wallpapers/"
  Mandelbrot.start
    .withDimensions(Dimensions.fujitsu)
    .withDynamAntiAliasedFractal(Mandelbrot.RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"dynam.png")

  Mandelbrot.start
    .withDimensions(Dimensions.fujitsu)
    .withAntiAliasedFractal(Mandelbrot.RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"aa.png")


  Mandelbrot.start
    .withDimensions(Dimensions.fujitsu)
    .withFractal(Mandelbrot.RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"basic.png")


}
