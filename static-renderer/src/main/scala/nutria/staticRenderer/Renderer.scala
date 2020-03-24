package nutria.staticRenderer

import nutria.core.FractalImage
import nutria.core.viewport.Dimensions
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom.raw.WebGLRenderingContext

import scala.scalajs.js.Dynamic
import scala.scalajs.js.typedarray.Uint8Array

object Renderer {
  def renderToFile(fractalImage: FractalImage, dimensions: Dimensions, fileName: String): Unit = {
    val context = gl(dimensions.width, dimensions.height, Dynamic.literal())

    val glProgram = FractalRenderer.constructProgram(context, fractalImage.program, fractalImage.antiAliase)
    FractalRenderer.render(context, fractalImage.view, glProgram)

    val buffer = new Uint8Array(dimensions.width * dimensions.height * 4)
    context.readPixels(0, 0, dimensions.width, dimensions.height, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, buffer)

    Jimp
      .read(
        Dynamic.literal(
          width = dimensions.width,
          height = dimensions.height,
          data = buffer
        ),
        (_, image) => image.write(fileName)
      )
  }
}
