import DefaultSaveFolder.defaultSaveFolder
import nutria.core.Dimensions
import nutria.core.content.{LinearNormalized, StreamByResolution}
import nutria.data.colors.Wikipedia
import nutria.data.consumers.CountIterations
import nutria.data.fractalFamilies.MandelbrotFamily
import nutria.data.sequences.Mandelbrot


object TestStream extends App {
  def fractal = Mandelbrot(500, 2) andThen
    CountIterations.smoothed() andThen
    LinearNormalized(0, 500) andThen
    Wikipedia

  val stream = StreamByResolution(MandelbrotFamily.initialViewport,
    Dimensions(16,9),
    8,
    fractal)

  import nutria.core.syntax._

  stream.zipWithIndex.foreach{
    case (img, i) => img.verboseSave(defaultSaveFolder /~ s"i_$i.png")
  }
}
