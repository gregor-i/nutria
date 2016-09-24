import nutria.color.HSV
import nutria.fractal.Mandelbrot
import nutria.fractal.techniques.EscapeTechniques
import nutria.syntax._
import nutria.viewport.Dimensions

object Main extends App {

  import Mandelbrot.{Sequence, start}

  val root = "/home/gregor/Pictures/Wallpapers/"
  start
    .withDimensions(Dimensions.fujitsu)
    .withDynamAntiAliasedFractal(EscapeTechniques[Sequence].RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"dynam.png")

  start
    .withDimensions(Dimensions.fujitsu)
    .withAntiAliasedFractal(EscapeTechniques[Sequence].RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"aa.png")


  start
    .withDimensions(Dimensions.fujitsu)
    .withFractal(EscapeTechniques[Sequence].RoughColoring(350))
    .linearNormalized
    .withColor(HSV.MonoColor.Blue)
    .verboseSave(root + s"basic.png")


}
