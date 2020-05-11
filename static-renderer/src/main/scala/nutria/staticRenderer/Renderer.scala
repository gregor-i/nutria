package nutria.staticRenderer

import nutria.core.{Dimensions, FractalImage}
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom.raw.WebGLRenderingContext

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dynamic
import scala.scalajs.js.typedarray.Uint8Array

object Renderer {
  def renderToFile(fractalImage: FractalImage, dimensions: Dimensions, fileName: String)(implicit ex: ExecutionContext): Future[Unit] = {
    val buffer = renderToBuffer(fractalImage, dimensions)
    saveToFile(buffer, fileName, dimensions)
  }

  def saveToFile(buffer: Uint8Array, fileName: String, dimensions: Dimensions)(implicit ex: ExecutionContext): Future[Unit] =
    Jimp
      .read(
        Dynamic.literal(
          width = dimensions.width,
          height = dimensions.height,
          data = buffer
        )
      )
      .toFuture
      .map(_.write(fileName))

  def renderToBuffer(fractalImage: FractalImage, dimensions: Dimensions): Uint8Array = {
    val context = gl(dimensions.width, dimensions.height, Dynamic.literal())

    val glProgram = FractalRenderer.constructProgram(context, fractalImage.program, fractalImage.antiAliase)
    FractalRenderer.render(context, fractalImage.view, glProgram)

    val buffer = new Uint8Array(dimensions.width * dimensions.height * 4)
    context.readPixels(0, 0, dimensions.width, dimensions.height, WebGLRenderingContext.RGBA, WebGLRenderingContext.UNSIGNED_BYTE, buffer)
    buffer
  }
}
