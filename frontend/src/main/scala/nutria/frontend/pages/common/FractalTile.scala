package nutria.frontend.pages.common

import nutria.core.{Dimensions, Examples, FractalImage}
import nutria.frontend.util.Untyped
import nutria.shaderBuilder.{FractalRenderer, WebWorkerData}
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.WebGLRenderingContext
import org.scalajs.dom.webworkers.Worker
import snabbdom.{Node, Snabbdom}

import scala.concurrent.{Future, Promise, TimeoutException}
import scala.scalajs.js.Dynamic
import scala.util.chaining._
import io.circe.syntax._
import nutria.frontend.pages.common.ImgStrategy.{Task, buffer, interval}

import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

object FractalTile {
  def apply(fractalImage: FractalImage, dimensions: Dimensions): Node =
    WebWorkerStrategy.render(fractalImage, dimensions)

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String =
    ImgStrategy.dataUrl(fractalImage, dimensions)
}

private object ImgStrategy {
  private lazy val canvas: Canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
  private lazy val webglCtx       = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

  private lazy val interval = dom.window.setInterval(
    () => {
      if (buffer.nonEmpty) {
        val task = buffer.dequeue()
        task.img.src = dataUrl(task.fractalImage, task.dimensions)
      }
    },
    100
  )

  private case class Task(img: Image, fractalImage: FractalImage, dimensions: Dimensions)

  private val buffer = scala.collection.mutable.Queue.empty[Task]

  def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("img")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .attr("src", "/assets/rendering.svg")
      .hook("insert", Snabbdom.hook { node =>
        val img = node.elm.get.asInstanceOf[Image]
        interval
        buffer.enqueue(Task(img, fractalImage, dimensions))
      })

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String = {
    canvas.width = dimensions.width
    canvas.height = dimensions.height
    FractalRenderer
      .compileProgram(webglCtx, fractalImage.template, fractalImage.antiAliase) match {
      case Right(webGlProgram) =>
        FractalRenderer.render(webglCtx, fractalImage.viewport, webGlProgram)
        canvas.toDataURL("image/png")
      case Left(_) => Images.compileError
    }
  }
}

object WebWorkerStrategy {
  def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("img")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .attr("src", "/assets/rendering.svg")
      .hook(
        "insert",
        Snabbdom.hook { node =>
          val img = node.elm.get.asInstanceOf[Image]
          WebWorkerInterface
            .sendRequest(fractalImage)
            .onComplete {
              case Success(dataUrl) => img.src = dataUrl
              case Failure(_)       => img.src = Images.compileError
            }
        }
      )
}

object WebWorkerInterface {
  private val timeout = 5000

  private val worker = new Worker("/assets/web-worker.js")

  private var runningRequests = Map.empty[WebWorkerData.CorrelationId, Promise[String]]

  worker.onmessage = event => {
    val data = event.data.asInstanceOf[WebWorkerData]
    runningRequests.get(data.correlationId) match {
      case Some(promise) =>
        promise.success(data.data)
      case None => ()
    }
  }

  var counter = 0
  def newCorrelationId(): WebWorkerData.CorrelationId = {
    counter = counter + 1
    counter
  }

  val payload: String = Examples.kochSnowflake
    .pipe(FractalImage.fromTemplate)
    .asJson
    .noSpaces

  def sendRequest(fractalImage: FractalImage): Future[String] = {
    val promise = Promise[String]()

    val correlationId = newCorrelationId()
    val payload       = fractalImage.asJson.noSpaces
    runningRequests += (correlationId -> promise)
    worker.postMessage(WebWorkerData(correlationId, payload))
    dom.window.setTimeout(() => if (!promise.isCompleted) promise.failure(new TimeoutException()), timeout)

    promise.future.onComplete(_ => runningRequests -= correlationId)
    promise.future
  }
}
