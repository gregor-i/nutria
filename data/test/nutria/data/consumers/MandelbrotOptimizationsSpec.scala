package nutria.data.consumers

import nurtia.data.consumers.{RoughColoring, SmoothColoring}
import nurtia.data.optimizations.MandelbrotOptimizations
import nurtia.data.sequences.Mandelbrot
import nutria.core.ContentFunction
import nutria.core.syntax._
import nutria.core.viewport.ViewportChooser.chooseFromUsefullStartPoints
import org.scalacheck.Gen.choose
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll
import org.specs2.scalacheck.Parameters
import org.specs2.{ScalaCheck, Specification}


trait FractalEquality{
  def fractalEquality[A](left:ContentFunction[A], right:ContentFunction[A]): Prop =
    forAll(chooseFromUsefullStartPoints){
      p => left(p._1, p._2) == right(p._1, p._2)
    }

}

class MandelbrotOptimizationsSpec extends Specification with ScalaCheck with FractalEquality {
  def is = s2""" MandelbrotOptimizationsSpec
              | There are optimized versions for:
              |   RoughColoring  $equalityForRoughColoring
              |   SmoothColoring $eqialityForSmoothColoring
              |""".stripMargin

  implicit val params: Parameters = Parameters(minTestsOk = 5000)

  def chooseIterations = choose(1, 1000)
  def chooseEscapeRadius = choose(2d, 1000d)

  def equalityForRoughColoring = forAll(chooseIterations, chooseEscapeRadius) {
    (iterations, escapeRadius) =>
      fractalEquality(
        Mandelbrot(iterations, escapeRadius) ~> RoughColoring.double(),
        MandelbrotOptimizations.RoughColoringDouble(iterations, escapeRadius)
      )
  }


  def eqialityForSmoothColoring = forAll(chooseIterations, chooseEscapeRadius) {
    (iterations, escapeRadius) =>
      fractalEquality(
        Mandelbrot(iterations, escapeRadius) ~> SmoothColoring(),
        MandelbrotOptimizations.SmoothColoring(iterations, escapeRadius)
      )
  }
}
