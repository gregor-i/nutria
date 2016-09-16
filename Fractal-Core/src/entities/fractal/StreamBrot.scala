package entities
package fractal

import spire.math.Complex
import spire.implicits._

object StreamBrot {
  case class RoughColoring(maxIteration: Int) extends Fractal {
    override def apply(x0: Double, y0: Double): Double = {
      val start = Complex(x0,y0)
      def seq: Stream[Complex[Double]] = Complex.zero[Double] #:: seq.map(c => c * c + start)
      seq.zipWithIndex.takeWhile{ case (v, i) => v.abs < 2 && i < maxIteration }.size
    }
  }
}
