package nutria.data.sequences

import nutria.core.Point
import mathParser.implicits._
import nutria.core.languages.{Lambda, Z, ZDer}
import nutria.data.colors.RGBA
import spire.math.Complex

object DerivedDivergingSeries {
  def apply(series: nutria.core.DerivedDivergingSeries): Point => RGBA = {
    val initialZ = series.initialZ.node
    val initialZDer =series.initialZDer.node

    val iterationZ = series.iterationZ.node
    val iterationZDer = series.iterationZDer.node

    val erq = series.escapeRadius.value * series.escapeRadius.value

    val v = Complex(Math.cos(series.angle.value), Math.sin(series.angle.value))

    val languageInitial = nutria.core.languages.lambda
    val languageIterationZ = nutria.core.languages.zAndLambda
    val languageIterationZDer = nutria.core.languages.zAndZDerAndLambda

    p => {
      val lambda = Complex(p._1, p._2)

      var z = languageInitial.evaluate(initialZ){case Lambda => lambda}
      var zDer = languageInitial.evaluate(initialZDer){case Lambda => lambda}

      var i = 0
      while(i < series.maxIterations.value && z.absSquare < erq){
        val zNext = languageIterationZ.evaluate(iterationZ){case Lambda => lambda; case Z => z}
        val zDerNext = languageIterationZDer.evaluate(iterationZDer){case Lambda => lambda; case Z => z; case ZDer => zDer}
        i += 1
        z = zNext
        zDer = zDerNext
      }

      if(series.maxIterations.value == i){
        RGBA(0d, 0d, 255d*0.25)
      }else{
        val d2 = z / zDer
        val u = d2 / d2.abs
        val t = Math.max((u.real * v.real + u.imag *v.imag + series.h2.value) / (1.0 + series.h2.value), 0.0)
        RGBA(t*255, t*255, t*255)
      }
    }
  }
}
