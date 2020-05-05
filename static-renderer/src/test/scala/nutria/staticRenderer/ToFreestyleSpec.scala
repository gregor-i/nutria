package nutria.staticRenderer

import nutria.core.viewport.Dimensions
import nutria.core.{DivergingSeries, FractalImage, FractalProgram, NewtonIteration, NormalMap, OuterDistance, ToFreestyle, Viewport}
import org.scalatest.Assertion
import org.scalatest.funsuite.AnyFunSuite

class ToFreestyleSpec extends AnyFunSuite {
  private def compare(fractalProgram: FractalProgram, viewport: Viewport): Assertion = {
    val asFreestyle = ToFreestyle(fractalProgram)

    val bufferNormal      = Renderer.renderToBuffer(FractalImage(fractalProgram, viewport), dimensions = Dimensions.thumbnail)
    val bufferToFreestyle = Renderer.renderToBuffer(FractalImage(asFreestyle, viewport), dimensions = Dimensions.thumbnail)

    assert(bufferNormal.toVector === bufferToFreestyle.toVector)
  }

  test("DivergingSeries.default") {
    compare(DivergingSeries.default, Viewport.mandelbrot)
  }

  test("DivergingSeries.default.copy(coloring = NormalMap())") {
    compare(DivergingSeries.default.copy(coloring = NormalMap()), Viewport.mandelbrot)
  }

  test("DivergingSeries.default.copy(coloring = OuterDistance())") {
    compare(DivergingSeries.default.copy(coloring = OuterDistance()), Viewport.mandelbrot)
  }

  test("NewtonIteration.default") {
    compare(NewtonIteration.default, Viewport.aroundZero)
  }
}
