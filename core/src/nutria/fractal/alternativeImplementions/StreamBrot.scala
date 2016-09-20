package nutria.fractal.alternativeImplementions

import nutria.Fractal
import spire.math.Complex
import spire.implicits._

object StreamBrot {
  def RoughColoring(maxIterations:Int):Fractal =
    (x0, y0) => {
      val start = Complex(x0,y0)
      def seq: Stream[Complex[Double]] = Complex.zero[Double] #:: seq.map(c => c * c + start)
      seq.zipWithIndex.takeWhile{ case (v, i) => v.abs < 2 && i < maxIterations }.size
    }
}
