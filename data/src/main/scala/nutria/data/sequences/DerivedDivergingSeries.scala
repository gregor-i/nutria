package nutria.data.sequences

import nutria.core.Point
import mathParser.implicits._
import nutria.core.derivedDivergingSeries._
import nutria.data.colors.RGBA
import spire.math.Complex

object DerivedDivergingSeries {
  def apply(series: nutria.core.DerivedDivergingSeries): Point => RGBA = {
    val initialZ = Language.initial.optimize(Language.initial.parse(series.initialZ).get)
    val initialZDer = Language.initial.optimize(Language.initial.parse(series.initialZDer).get)

    val iterationZ = Language.iterationZ.optimize(Language.iterationZ.parse(series.iterationZ).get)
    val iterationZDer = Language.iterationZDer.optimize(Language.iterationZDer.parse(series.iterationZDer).get)

    val erq = series.escapeRadius * series.escapeRadius

    val v = Complex(Math.cos(series.angle), Math.sin(series.angle))

    p => {
      val lambda = Complex(p._1, p._2)

      var z = Language.initial.evaluate(initialZ){case Lambda => lambda}
      var zDer = Language.initial.evaluate(initialZDer){case Lambda => lambda}

      var i = 0
      while(i < series.maxIterations && z.absSquare < erq){
        val zNext = Language.iterationZ.evaluate(iterationZ){case Lambda => lambda; case Z => z}
        val zDerNext = Language.iterationZDer.evaluate(iterationZDer){case Lambda => lambda; case Z => z; case ZDer => zDer}
        i += 1
        z = zNext
        zDer = zDerNext
      }

      if(series.maxIterations == i){
        RGBA(0d, 0d, 255d*0.25)
      }else{
        val d2 = z / zDer
        val u = d2 / d2.abs
        val t = Math.max((u.real * v.real + u.imag *v.imag + series.h2) / (1.0 + series.h2), 0.0)
        RGBA(t*255, t*255, t*255)
      }
    }
  }
}
