import nutria.core.RGBA
import nutria.data.Defaults
import nutria.data.colors.Wikipedia
import nutria.data.consumers.CountIterations
import nutria.data.content.LinearNormalized
import nutria.data.image.Image
import nutria.data.sequences.Mandelbrot
import nutria.data.syntax._

object SimpleExamples extends App with Defaults {

  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.double() andThen LinearNormalized(0d, 350d))
      .withColor(Wikipedia), RGBA.white, defaultSaveFolder /~ "basic.png")

  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.smoothed() andThen LinearNormalized(0d, 350d))
      .withColor(Wikipedia), RGBA.white, defaultSaveFolder /~ "basic-smoothed.png")


  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.double() andThen LinearNormalized(0d, 350d))
      .multisampled()
      .withColor(defaultColor), RGBA.white, defaultSaveFolder /~ "basic-aa.png")

  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.smoothed() andThen LinearNormalized(0d, 350d))
      .multisampled()
      .withColor(defaultColor), RGBA.white, defaultSaveFolder /~ "basic-smoothe-aa.png")
}
