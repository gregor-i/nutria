package nutria.staticRenderer

import nutria.core.FractalImage
import nutria.core.viewport.Dimensions
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}

trait RenderingSuite extends AsyncFunSuite {
  implicit override def executionContext = scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  val baseFolder = s"./temp/${getClass.getSimpleName}"

  def renderingTest(testName: String)(fractal: => FractalImage, fileName: String, dimensions: Dimensions = Dimensions.fullHD) =
    test(testName) {
      Renderer
        .renderToFile(
          fractalImage = fractal,
          dimensions = dimensions,
          fileName = fileName
        )
        .map(_ => succeed)
    }
}