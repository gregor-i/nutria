package nutria.staticRenderer

import nutria.core.viewport.Dimensions
import nutria.core.{DivergingSeries, FractalImage}

object Main {
  def main(args: Array[String]): Unit = {
    Renderer.renderToFile(
      fractalImage = FractalImage(
        DivergingSeries.default
      ),
      dimensions = Dimensions.fullHD,
      fileName = "./test.png"
    )
  }
}
