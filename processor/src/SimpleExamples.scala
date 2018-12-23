import nutria.core.content.LinearNormalized
import nutria.core.syntax._
import nutria.data.Defaults
import nutria.data.colors.Wikipedia
import nutria.data.consumers.CountIterations
import nutria.data.sequences.Mandelbrot

object SimpleExamples extends App with Defaults {

  defaultViewport
    .withDimensions(defaultDimensions)
    .withContent(Mandelbrot(350, 10d) andThen CountIterations.double() andThen LinearNormalized(0d, 350d))
    .withColor(Wikipedia)
    .verboseSave(defaultSaveFolder /~ "basic.png")

  defaultViewport
    .withDimensions(defaultDimensions)
    .withContent(Mandelbrot(350, 10d) andThen CountIterations.smoothed() andThen LinearNormalized(0d, 350d))
    .withColor(Wikipedia)
    .verboseSave(defaultSaveFolder /~ "basic-smoothed.png")


  defaultViewport
    .withDimensions(defaultDimensions)
    .withContent(Mandelbrot(350, 10d) andThen CountIterations.double() andThen LinearNormalized(0d, 350d))
    .multisampled()
    .withColor(defaultColor)
    .verboseSave(defaultSaveFolder /~ "basic-aa.png")

  defaultViewport
    .withDimensions(defaultDimensions)
    .withContent(Mandelbrot(350, 10d) andThen CountIterations.smoothed() andThen LinearNormalized(0d, 350d))
    .multisampled()
    .withColor(defaultColor)
    .verboseSave(defaultSaveFolder /~ "basic-smoothe-aa.png")
}
