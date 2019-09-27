import nutria.core.{Dimensions, RGBA}
import nutria.data.Defaults
import nutria.data.colors.Wikipedia
import nutria.data.consumers.CountIterations
import nutria.data.content.{LinearNormalized, StreamByResolution}
import nutria.data.fractalFamilies.MandelbrotFamily
import nutria.data.image.Image
import nutria.data.sequences.Mandelbrot

object TestStream extends App with Defaults {
  def fractal = Mandelbrot(500, 2) andThen
    CountIterations.smoothed() andThen
    LinearNormalized(0, 500) andThen
    Wikipedia

  val stream = StreamByResolution(MandelbrotFamily.initialViewport,
    Dimensions(16, 9),
    8,
    fractal)

  stream.zipWithIndex.foreach {
    case (img, i) => Image.verboseSave(img, RGBA.white, defaultSaveFolder /~ s"i_$i.png")
  }
}
