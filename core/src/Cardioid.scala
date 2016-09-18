import entities.Color
import entities.color.{HSV, Invert}
import entities.fractal.Mandelbrot
import entities.fractal.technics.CardioidTechnics
import entities.syntax._
import entities.viewport.{Dimensions, Fokus, Viewport, ViewportUtil}

object Cardioid extends App {
    def apply(view:Viewport, path:(Viewport, Color) => String) {
      val content =
        view
          .withDimensions(Dimensions.fullHD)
          .withFractal(Mandelbrot.CardioidNumeric(500, 50))
          //.withAntiAliasedFractal(Mandelbrot.CardioidNumeric(500, 50))
          .cached

      val normalized =
        content
          .strongNormalized

      normalized
        .withColor(HSV.MonoColor.Blue)
        .save(path(view, HSV.MonoColor.Blue))

      normalized
        .withColor(Invert(HSV.MonoColor.Blue))
        .save(path(view, Invert(HSV.MonoColor.Blue)))
    }

  def createImages() = {
    apply(Mandelbrot.start, (view, color) => s"images/start_$color.png")

    for (viewport <- Viewport.auswahl)
      apply(viewport, (view, color) => s"images/auswahl/$view/$color.png")

    for (viewport <- Fokus.iteration2)
      apply(viewport, (view, color) => s"images/fokus/$view/$color.png")
  }

  createImages()


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
