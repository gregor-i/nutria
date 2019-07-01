import DefaultSaveFolder.defaultSaveFolder
import image.Image
import nutria.core.content.LinearNormalized
import nutria.core.syntax._
import nutria.data.Defaults
import nutria.data.colors.Wikipedia
import nutria.data.consumers.CountIterations
import nutria.data.sequences.Mandelbrot

object SimpleExamples extends App with Defaults {

  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.double() andThen LinearNormalized(0d, 350d))
      .withColor(Wikipedia), defaultSaveFolder /~ "basic.png")

  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.smoothed() andThen LinearNormalized(0d, 350d))
      .withColor(Wikipedia), defaultSaveFolder /~ "basic-smoothed.png")


  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.double() andThen LinearNormalized(0d, 350d))
      .multisampled()
      .withColor(defaultColor), defaultSaveFolder /~ "basic-aa.png")

  Image.verboseSave(
    defaultViewport
      .withDimensions(defaultDimensions)
      .withContent(Mandelbrot(350, 10d) andThen CountIterations.smoothed() andThen LinearNormalized(0d, 350d))
      .multisampled()
      .withColor(defaultColor), defaultSaveFolder /~ "basic-smoothe-aa.png")
}
