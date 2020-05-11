package nutria.staticRenderer

import nutria.core.viewport.Dimensions
import nutria.core.{Examples, FractalImage}

class ExampleFractalSpec extends RenderingSuite {
  val dimensions = Dimensions.fullHD

  for {
    (name, program, viewport) <- Examples.allNamed
  } {
    renderingTest(s"renders example: $name")(
      fractal = FractalImage(program, viewport),
      dimensions = dimensions,
      fileName = s"${baseFolder}/${name}.png"
    )
  }
}
