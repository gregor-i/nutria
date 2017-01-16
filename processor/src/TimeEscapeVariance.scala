import nurtia.data.Defaults
import nurtia.data.colors.RGBGradient
import nurtia.data.consumers.SmoothColoring
import nurtia.data.optimizations.MandelbrotOptimizations
import nurtia.data.sequences.Mandelbrot
import nutria.core.accumulator.Variance
import nutria.core.colors.RGB
import nutria.core.syntax._
import processorHelper.ProcessorHelper

object TimeEscapeVariance extends Defaults with ProcessorHelper {
  def main(args:Array[String]): Unit = {
    val cached = defaultViewport
      .withDimensions(default)
      .withAntiAliasedFractal(MandelbrotOptimizations.SmoothColoring(1000, 100d), Variance, 5)
      .cached

    cached
      .strongNormalized
      .withColor(RGBGradient(RGB.white, RGB.black))
      .save(defaultSaveFolder /~ "TimeEscapeVariance (strong).png")

    cached
      .linearNormalized
      .withColor(RGBGradient(RGB.white, RGB.black))
      .save(defaultSaveFolder /~ "TimeEscapeVariance (linear).png")
  }
}
