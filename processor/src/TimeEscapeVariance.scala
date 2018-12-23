import nutria.core.accumulator.Variance
import nutria.core.syntax._
import nutria.data.Defaults
import nutria.data.colors.WhiteToBlack
import nutria.data.consumers.CountIterations
import nutria.data.sequences.Mandelbrot
import processorHelper.ProcessorHelper

object TimeEscapeVariance extends Defaults with ProcessorHelper {
  def main(args: Array[String]): Unit = {
    val cached = defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(1000, 100d) andThen CountIterations.smoothed())
      .multisampled(Variance, 5)
      .cached

    cached
      .linearNormalized
      .withColor(WhiteToBlack)
      .save(defaultSaveFolder /~ "TimeEscapeVariance.png")
  }
}
