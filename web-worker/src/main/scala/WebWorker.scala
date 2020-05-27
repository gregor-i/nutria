import nutria.core.{Dimensions, Examples, FractalImage, FractalTemplate, Viewport}
import nutria.shaderBuilder.{FractalRenderer, WebWorkerData}
import org.scalajs.dom.raw.URL
import org.scalajs.dom.webworkers.DedicatedWorkerGlobalScope.self

import scala.scalajs.js.Dynamic
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import io.circe.parser.parse
import io.circe.syntax._
import scala.util.chaining._

object WebWorker {
  def main(args: Array[String]): Unit = {
    self.onmessage = event => {
      val data: WebWorkerData = event.data.asInstanceOf[WebWorkerData]
      val correlationId       = data.correlationId
      val payload             = data.data.pipe(parse).flatMap(_.as[FractalImage])

      val fractalImage = payload.getOrElse(???)

      OffscreenCanvasRenderer.renderIntoObjectUrl(fractalImage).onComplete {
        case Success(objectUrl) =>
          self.postMessage(WebWorkerData(correlationId, objectUrl))
        case Failure(exception) =>
          Dynamic.global.console.log("error", exception.getMessage)
      }
    }
  }
}

private object OffscreenCanvasRenderer {
  private val canvas  = new OffscreenCanvas(Dimensions.thumbnail.width, Dimensions.thumbnail.height)
  private val context = canvas.getContext("webgl")

  def renderIntoObjectUrl(fractalImage: FractalImage): Future[String] = {
    val template        = FractalTemplate.applyParameters(fractalImage.template)(fractalImage.parameters)
    val compiledProgram = FractalRenderer.compileProgram(context, template, fractalImage.antiAliase)
    for {
      fractalProgram <- Future.fromTry(compiledProgram.toTry)
      _ = FractalRenderer.render(context, fractalImage.viewport, fractalProgram)
      blob <- canvas.convertToBlob(None).toFuture
      url = URL.createObjectURL(blob)
    } yield url
  }
}
