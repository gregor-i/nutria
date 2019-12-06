package nutria.frontend.ui.common

import nutria.core.{Dimensions, FractalImage}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
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
}

private sealed trait Strategy {
  def render(fractalImage: FractalImage, dimensions: Dimensions): Node
}

private object OffscrencanvasStrategy extends Strategy {
  private lazy val untypedWindow = Untyped(dom.window)
  private lazy val offscreenCanvas = Dynamic.newInstance(untypedWindow.OffscreenCanvas)(0, 0)
  private lazy val webglCtx = Untyped(offscreenCanvas).getContext("webgl", Dynamic.literal(preserveDrawingBuffer = true)).asInstanceOf[WebGLRenderingContext]

  override def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("canvas")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .hook("insert", Snabbdom.hook { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        dom.window.setTimeout(() => {
          val webGlProgram = FractalRenderer.constructProgram(webglCtx, fractalImage.program, fractalImage.antiAliase)
          offscreenCanvas.width = dimensions.width
          offscreenCanvas.height = dimensions.height
          FractalRenderer.render(webglCtx, fractalImage.view, webGlProgram)
          canvas.getContext("bitmaprenderer").transferFromImageBitmap(offscreenCanvas.transferToImageBitmap())
        }, 5)
      })
}

private object ImgStrategy extends Strategy {
  private lazy val canvas: Canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
  private lazy val webglCtx = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

  dom.window.setInterval(() => {
    if (buffer.nonEmpty) {
      val task = buffer.dequeue()
      val webGlProgram = FractalRenderer.constructProgram(webglCtx, task.fractalImage.program, task.fractalImage.antiAliase)
      canvas.width = task.dimensions.width
      canvas.height = task.dimensions.height
      FractalRenderer.render(webglCtx, task.fractalImage.view, webGlProgram)
      task.img.src = canvas.toDataURL("image/png")
    }
  }, 100)

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
        buffer.enqueue(Task(img, fractalImage, dimensions))
      })
}