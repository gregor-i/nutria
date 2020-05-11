package nutria.staticRenderer

import nutria.core.viewport.Dimensions
import nutria.core.{Examples, FractalImage}

class ExampleFractalSpec extends RenderingSuite {
  for {
    (name, program, viewport) <- Examples.allNamed
  } {
    renderingTest(s"renders example: $name")(
      fractal = FractalImage(program, viewport),
      dimensions = Dimensions.fullHD,
      fileName = s"${baseFolder}/${name}.png"
    )
  }
}
