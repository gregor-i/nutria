package nutria.frontend.ui.common

import nutria.core.{Dimensions, FractalEntity, FractalImage, FractalProgram, Viewport}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, SnabbdomFacade, VNode}

import scala.scalajs.js.Dynamic

object FractalTile {
  // TODO: somehow OffscrencanvasStrategy doesn't work on chromium ....
  private val strategy = ImgStrategy
  //    if (Untyped(dom.window).OffscreenCanvas.asInstanceOf[UndefOr[_]].isDefined)
  //      OffscrencanvasStrategy
  //    else
  //      ImgStrategy

  def apply(fractalImage: FractalImage, dimensions: Dimensions): VNode =
    strategy.render(fractalImage, dimensions)
}

private sealed trait Strategy {
  def render(fractalImage: FractalImage, dimensions: Dimensions): VNode
}

private object OffscrencanvasStrategy extends Strategy {
  private lazy val untypedWindow = Untyped(dom.window)
  private lazy val offscreenCanvas = Dynamic.newInstance(untypedWindow.OffscreenCanvas)(0, 0)
  private lazy val webglCtx = Untyped(offscreenCanvas).getContext("webgl", Dynamic.literal(preserveDrawingBuffer = true)).asInstanceOf[WebGLRenderingContext]

  override def render(fractalImage: FractalImage, dimensions: Dimensions): VNode =
    h("canvas",
      key = fractalImage.hashCode(),
      attrs = Seq("width" -> dimensions.width.toString, "height" -> dimensions.height.toString),
      hooks = Seq[(String, SnabbdomFacade.Hook)](
        "insert" -> Snabbdom.hook { node =>
          val canvas = node.elm.get.asInstanceOf[Canvas]
          dom.window.setTimeout(() => {
            val webGlProgram = FractalRenderer.constructProgram(webglCtx, fractalImage.program, fractalImage.antiAliase)
            offscreenCanvas.width = dimensions.width
            offscreenCanvas.height = dimensions.height
            FractalRenderer.render(webglCtx, fractalImage.view, webGlProgram)
            canvas.getContext("bitmaprenderer").transferFromImageBitmap(offscreenCanvas.transferToImageBitmap())
          }, 5)
        }
      ))()
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

  override def render(fractalImage: FractalImage, dimensions: Dimensions): VNode =
    h("img",
      key = fractalImage.hashCode(),
      attrs = Seq(
        "width" -> dimensions.width.toString,
        "height" -> dimensions.height.toString,
        "src" -> "/img/rendering.svg"
      ),
      hooks = Seq[(String, SnabbdomFacade.Hook)](
        "insert" -> Snabbdom.hook { node =>
          val img = node.elm.get.asInstanceOf[Image]
          buffer.enqueue(Task(img, fractalImage, dimensions))
        }
      )
    )()
}