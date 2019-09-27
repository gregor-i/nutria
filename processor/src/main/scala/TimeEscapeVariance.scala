import nutria.core.RGBA
import nutria.data.Defaults
import nutria.data.accumulator.Variance
import nutria.data.colors.WhiteToBlack
import nutria.data.consumers.CountIterations
import nutria.data.image.Image
import nutria.data.sequences.Mandelbrot
import nutria.data.syntax._
import processorHelper.ProcessorHelper

object TimeEscapeVariance extends Defaults with ProcessorHelper {
  def main(args: Array[String]): Unit = {
    val cached = defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(1000, 100d) andThen CountIterations.smoothed())
      .multisampled(Variance, 5)
      .cached

    Image.save(
      cached
        .linearNormalized
        .withColor(WhiteToBlack), RGBA.white, defaultSaveFolder /~ "TimeEscapeVariance.png")
  }
}
