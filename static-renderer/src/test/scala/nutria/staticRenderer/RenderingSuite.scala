package nutria.staticRenderer

import nutria.core.{Dimensions, FractalImage}
import org.scalatest.funsuite.{AnyFunSuite, AsyncFunSuite}

import scala.util.{Failure, Success}

trait RenderingSuite extends AnyFunSuite {
  val baseFolder = s"./temp/${getClass.getSimpleName}"

  def renderingTest(testName: String)(fractal: => FractalImage, fileName: String, dimensions: Dimensions = Dimensions.fullHD) =
    test(testName) {
      val result = Renderer
        .renderToFile(
          fractalImage = fractal,
          dimensions = dimensions,
          fileName = fileName
        )
      result match {
        case Success(value)     => succeed
        case Failure(exception) => fail(exception)
      }
    }
}
