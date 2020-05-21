package nutria.staticRenderer

import nutria.core.{Dimensions, Examples, FractalImage}

class ExampleFractalSpec extends RenderingSuite {
  for {
    (name, template) <- Examples.allNamed
  } {
    renderingTest(s"renders example: $name")(
      fractal = FractalImage(template, template.exampleViewport),
      dimensions = Dimensions.fullHD,
      fileName = s"${baseFolder}/${name}.png"
    )
  }
}
