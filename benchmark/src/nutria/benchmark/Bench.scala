package nutria
package benchmark

import nutria.content._
import nutria.fractal._
import nutria.fractal.alternativeImplementions.{SpireBrot, StreamBrot}
import nutria.syntax._
import nutria.viewport._
import spire.math.Quaternion

class Bench {
   def operation(fractal: Fractal): CachedContent =
     Mandelbrot.start
      .withDimensions(Dimensions.fujitsu.scale(0.1))
      .withAntiAliasedFractal(fractal)
      .cached

  //@Benchmark
  def mandelRough = operation(Mandelbrot.RoughColoring(500))
  @Benchmark
  def spireRough = operation(SpireBrot.RoughColoring(500))
  //@Benchmark
  def streamRough = operation(StreamBrot.RoughColoring(500))
  //@Benchmark
  def quatbRough = operation(QuatBrot.RoughColoring(500){ case (x,y) => Quaternion(x,y,0,0) })

}
