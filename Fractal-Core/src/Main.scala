import entities.viewport.{Dimensions, ViewportUtil}
import entities.fractal.Mandelbrot
import entities.color.HSV
import entities.fractal.QuatBrot
import entities.syntax._
import spire.math.Quaternion
import scala.sys.process._

object Main extends App {
  val root = "/home/merlin/brot/"
  val rendered = for (i <- 0 to 0 by 1) yield {
    ViewportUtil.start
      .withDimensions(Dimensions.lenovoX1.scale(10))
      .withFractal(QuatBrot.RoughColoring(50) { case (x, y) => Quaternion(x, y, 0, 0) })
      .linearNormalized.withColor(HSV.Rainbow)
      .verboseSave(root + s"quatbrot$i.png")
  }
  val comparison = ViewportUtil.start
    .withDimensions(Dimensions.lenovoX1)
    .withFractal(Mandelbrot.RoughColoring(50))
    .linearNormalized.withColor(HSV.MonoColor.Red)
    .verboseSave(root + "mandelbrot.png")

  val files = rendered.map(_.getAbsolutePath)
  ("feh " + files.mkString(" ")).!
  files.foreach(file => ("rm " + file).!)
}
