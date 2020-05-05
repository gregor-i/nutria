package nutria.staticRenderer

import nutria.core.viewport.Dimensions
import nutria.core.FractalImage
import org.scalatest.funsuite.AnyFunSuite
import nutria.SystemFractals.systemFractals

import scala.concurrent.Future

class SystemFractalSpec extends RenderingSuite {
  val dimensions = Dimensions.thumbnail

  for {
    (fractalImage, index) <- FractalImage.allImages(systemFractals).zipWithIndex
  } renderingTest(s"renders all system fractals ($index)")(
    fractal = fractalImage,
    dimensions = Dimensions.thumbnail,
    fileName = s"${baseFolder}/${index}.png"
  )
}
