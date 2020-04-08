package nutria.frontend.pages.common

import nutria.core.{Dimensions, FractalImage}
import nutria.frontend.util.Untyped
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.{Node, Snabbdom}

import scala.scalajs.js.Dynamic

object FractalTile {
  // TODO: somehow OffscrencanvasStrategy doesn't work on chromium ....
  private val strategy = ImgStrategy
  //    if (Untyped(dom.window).OffscreenCanvas.asInstanceOf[UndefOr[_]].isDefined)
  //      OffscrencanvasStrategy
  //    else
  //      ImgStrategy

  def apply(fractalImage: FractalImage, dimensions: Dimensions): Node =
    strategy.render(fractalImage, dimensions)

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String =
    strategy.dataUrl(fractalImage, dimensions)
}

private sealed trait Strategy {
  def render(fractalImage: FractalImage, dimensions: Dimensions): Node
  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String
}

private object OffscrencanvasStrategy extends Strategy {
  private lazy val untypedWindow   = Untyped(dom.window)
  private lazy val offscreenCanvas = Dynamic.newInstance(untypedWindow.OffscreenCanvas)(0, 0)
  private lazy val webglCtx = Untyped(offscreenCanvas)
    .getContext("webgl", Dynamic.literal(preserveDrawingBuffer = true))
    .asInstanceOf[WebGLRenderingContext]

  override def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("canvas")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .hook(
        "insert",
        Snabbdom.hook { node =>
          val canvas = node.elm.get.asInstanceOf[Canvas]
          dom.window.setTimeout(
            () => {
              offscreenCanvas.width = dimensions.width
              offscreenCanvas.height = dimensions.height
              val webGlProgram = FractalRenderer
                .constructProgram(webglCtx, fractalImage.program, fractalImage.antiAliase)
              FractalRenderer.render(webglCtx, fractalImage.view, webGlProgram)
              canvas
                .getContext("bitmaprenderer")
                .transferFromImageBitmap(offscreenCanvas.transferToImageBitmap())
            },
            5
          )
        }
      )

  override def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String = ???
}

private object ImgStrategy extends Strategy {
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

  override def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("img")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .attr("src", "/img/rendering.svg")
      .hook("insert", Snabbdom.hook { node =>
        val img = node.elm.get.asInstanceOf[Image]
        interval
        buffer.enqueue(Task(img, fractalImage, dimensions))
      })

  override def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String = {
    canvas.width = dimensions.width
    canvas.height = dimensions.height
    val webGlProgram = FractalRenderer
      .constructProgram(webglCtx, fractalImage.program, fractalImage.antiAliase)
    FractalRenderer.render(webglCtx, fractalImage.view, webGlProgram)
    canvas.toDataURL("image/png")
  }
}
