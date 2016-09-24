package nutria
package benchmark

import nutria.content._
import nutria.fractal._
import nutria.fractal.alternativeImplementions.{SpireBrot, StreamBrot}
import nutria.fractal.techniques.EscapeTechniques
import nutria.syntax._
import nutria.viewport._
import org.openjdk.jmh.annotations.Benchmark
import spire.math.Quaternion

class Bench {
  def operation(fractal: Fractal): CachedContent =
    Mandelbrot.start
      .withDimensions(Dimensions.fujitsu.scale(0.1))
      .withAntiAliasedFractal(fractal)
      .cached

  @Benchmark
  def mandelRough = operation(EscapeTechniques[Mandelbrot.Sequence].RoughColoring(500))

  @Benchmark
  def spireRough = operation(EscapeTechniques[SpireBrot.Sequence].RoughColoring(500))

  @Benchmark
  def streamRough = operation(StreamBrot.RoughColoring(500))

  @Benchmark
  def quatbRough = operation{
    val quat = new QuaternionBrot((x, y) => Quaternion(x, y, 0, 0))
    import quat.seqConstructor
    EscapeTechniques[quat.Sequence].RoughColoring(500)
  }

}
