package nutria.frontend.ui.common

import nutria.core.{Dimensions, FractalEntity}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, SnabbdomNative, VNode}

import scala.scalajs.js.Dynamic

object FractalImage {
  // TODO: somehow OffscrencanvasStrategy doesn't work on chromium ....
  private val strategy = ImgStrategy
  //    if (Untyped(dom.window).OffscreenCanvas.asInstanceOf[UndefOr[_]].isDefined)
//      OffscrencanvasStrategy
//    else
//      ImgStrategy

  def apply(fractalEntity: FractalEntity, dimensions: Dimensions): VNode =
    strategy.render(fractalEntity, dimensions)
}

private sealed trait Strategy {
  def render(fractalEntity: FractalEntity, dimensions: Dimensions): VNode
}

private object OffscrencanvasStrategy extends Strategy {
  private lazy val untypedWindow = Untyped(dom.window)
  private lazy val offscreenCanvas = Dynamic.newInstance(untypedWindow.OffscreenCanvas)(0, 0)
  private lazy val webglCtx = Untyped(offscreenCanvas).getContext("webgl", Dynamic.literal(preserveDrawingBuffer = true)).asInstanceOf[WebGLRenderingContext]

  override def render(fractalEntity: FractalEntity, dimensions: Dimensions): VNode =
    h("canvas",
      key = fractalEntity.hashCode(),
      attrs = Seq("width" -> dimensions.width.toString, "height" -> dimensions.height.toString),
      hooks = Seq[(String, SnabbdomNative.Hook)](
        "insert" -> Snabbdom.hook { node =>
          val canvas = node.elm.get.asInstanceOf[Canvas]
          dom.window.setTimeout(() => {
            val webGlProgram = FractalRenderer.constructProgram(webglCtx, fractalEntity.program, fractalEntity.antiAliase)
            offscreenCanvas.width = dimensions.width
            offscreenCanvas.height = dimensions.height
            FractalRenderer.render(webglCtx, fractalEntity.view, webGlProgram)
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
      val webGlProgram = FractalRenderer.constructProgram(webglCtx, task.entity.program, task.entity.antiAliase)
      canvas.width = task.dimensions.width
      canvas.height = task.dimensions.height
      FractalRenderer.render(webglCtx, task.entity.view, webGlProgram)
      task.img.src = canvas.toDataURL("image/png")
    }
  }, 100)

  private case class Task(img: Image, entity: FractalEntity, dimensions: Dimensions)

  private val buffer = scala.collection.mutable.Queue.empty[Task]

  override def render(fractalEntity: FractalEntity, dimensions: Dimensions): VNode =
    h("img",
      key = fractalEntity.hashCode(),
      attrs = Seq(
        "width" -> dimensions.width.toString,
        "height" -> dimensions.height.toString,
        "src" -> "/img/rendering.svg"
      ),
      hooks = Seq[(String, SnabbdomNative.Hook)](
        "insert" -> Snabbdom.hook { node =>
          val img = node.elm.get.asInstanceOf[Image]
          buffer.enqueue(Task(img, fractalEntity, dimensions))
        }
      )
    )()
}