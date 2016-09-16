package nutria

import org.openjdk.jmh.annotations._
import entities.fractal._
import entities.content._
import entities.viewport._
import entities.syntax._
import spire.math.Quaternion

class Bench {
   def operation(fractal: Fractal): CachedContent =
    entities.viewport.Viewport.benchmark
      .withDimensions(Dimensions.screenHD.scale(0.1))
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
