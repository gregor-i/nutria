import nutria.core.viewport.Dimensions
import nutria.core.{DivergingSeries, FractalImage, FreestyleProgram}
import org.scalajs.dom.raw.WebGLRenderingContext

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import nutria.shaderBuilder.FractalRenderer

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.scalajs.js.{Dynamic, Promise}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.chaining._
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.util.Try
import scala.scalajs.js.Thenable.Implicits._

@js.native
@JSImport("gl", JSImport.Namespace)
object gl extends js.Object {
  def apply(width: Int, height: Int, options: js.Object): WebGLRenderingContext = js.native
}

@js.native
@JSImport("jimp", JSImport.Namespace)
object Jimp extends js.Object {
  def read(read: js.Object, callback: js.Function2[js.Object, JimpImage, Unit]): Promise[Unit] = js.native
}

@js.native
trait JimpImage extends js.Object {
  def write(file: String): Unit = js.native
}

object Main {
  def main(args: Array[String]): Unit = {
    renderToFile(
      fractalImage = FractalImage(
        DivergingSeries.default
      ),
      dimensions = Dimensions.fullHD,
      fileName = "./test.png"
    )
  }

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
