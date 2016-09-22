import java.io.File

import nutria.Color
import nutria.color.{HSV, Invert}
import nutria.fractal.Mandelbrot
import nutria.syntax._
import nutria.viewport.{Dimensions, Viewport}
import viewportSelections.ViewportSelection

object Cardioid extends ProcessorHelper {
  override def rootFolder: String = "/home/gregor/Pictures/Cardioid/"

  override def statusPrints: Boolean = true

  def make(view: Viewport, path: Color => File): Unit = {
    view
      .withDimensions(Dimensions.fullHD)
//      .withFractal(Mandelbrot.CardioidNumeric(2000, 75))
      .withAntiAliasedFractal(Mandelbrot.CardioidNumeric(2000, 75), samplingFactor = 2)
      .strongNormalized
      .fanOut(
        _.withColor(HSV.MonoColor.Blue)
          .save(path(HSV.MonoColor.Blue)),
        _.withColor(Invert(HSV.MonoColor.Blue))
          .save(path(Invert(HSV.MonoColor.Blue)))
      )
  }

  def main(args: Array[String]): Unit = {
    val tasks1:Set[Task] = Set(
      (() => false,
        () => make(Mandelbrot.start, color => fileInRootFolder(s"start_$color.png"))))

    val tasks2:Set[Task] = for (viewport <- ViewportSelection.selection)
      yield (() => fileInRootFolder(s"auswahl/$viewport").exists,
        () => make(viewport, color => fileInRootFolder(s"auswahl/$viewport/$color.png")))

    val tasks3:Set[Task] = for (viewport <- ViewportSelection.focusIteration2)
      yield (() => fileInRootFolder(s"fokus/$viewport").exists,
        () => make(viewport, color => fileInRootFolder(s"fokus/$viewport/$color.png")))

    makeAll(tasks1.toSeq)
//    makeAll(tasks3)
    makeAll(tasks2.toSeq)
  }


  //  def checkNewton() = {
  //    val card = Mandelbrot.CardioidNumeric(50, 10)
  //
  //    val (x0, y0) = (-0.6338249269532736, 0.37772463334418416)
  //    /*for((x, y) <- new Mandelbrot.Iterator(x0, y0, 50).wrapped) {
  //      val g = card.golden(x, y)
  //      val n = card.newton(g, x, y)
  //      val m = card.minimalDistance(x, y)
  //      println(s"$x, $y, $g, ${card.dist(g, x, y)}, $n, ${card.dist(n, x, y)}")
  //    }*/
  //    for(t <- 2d until 3 by 0.001)
  //      println(s"$t, ${CardioidTechnics.dist(t, x0, y0)}, ${CardioidTechnics.d_derived(t, x0, y0)}, ${CardioidTechnics.d_derived2(t, x0, y0)}")
  //  }

  //  def checkAbls() = {
  //    val card = Mandelbrot.CardioidNumeric(50, 10)
  //    val check = (for{
  //      x <- -2d to 2d by 0.1
  //      y <- -2d to 2d by 0.1
  //      t <- -2d to 2d by 0.1
  //      d = CardioidTechnics.der1DivDer2(t, x, y)
  //      d1c = CardioidTechnics.d_derived_ana(t, x, y)
  //      d2c = CardioidTechnics.d_derived2_ana(t, x, y)
  //    } yield {
  //      val f = (d - (d1c / d2c)).abs < 1e-10
  //      if(!f)println(d - (d1c / d2c))
  //      f
  //    }).forall(identity)
  //    println(check)
  //  }

  //checkNewton()
  //  checkAbls()

  //  val card = Mandelbrot.CardioidNumeric(50, 10)
  //
  //  for {
  //    x <- -2.0 to 2.0 by 0.1
  //    y <- -2.0 to 2.0 by 0.1
  //    t <- -2.0 to 2.0 by 0.1
  //  } {
  ////    println(s"1 num: ${card.d_derived(t, x, y)}; ana: ${card.d_derived_ana(t, x, y)}")
  //   println (s"2 diff: ${card.d_derived2(t, x, y) - card.d_derived2_ana(t, x, y)}; num: ${card.d_derived2(t, x, y)}; ana: ${card.d_derived2_ana(t, x, y)}")
  //}
}
