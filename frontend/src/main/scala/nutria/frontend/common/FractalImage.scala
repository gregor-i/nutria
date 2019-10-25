package nutria.frontend.common

import nutria.core.{Dimensions, FractalEntity}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.Snabbdom.h
import snabbdom.{Snabbdom, SnabbdomNative, VNode}

import scala.scalajs.js.Dynamic

object FractalImage {
  private val untypedWindow = Untyped(dom.window)
  private val offscreenCanvas = Dynamic.newInstance(untypedWindow.OffscreenCanvas)(0, 0)
  private val webglCtx = Untyped(offscreenCanvas).getContext("webgl").asInstanceOf[WebGLRenderingContext]

  def apply(fractalEntity: FractalEntity, dimensions: Dimensions): VNode =
    h("canvas",
      hooks = Seq[(String, SnabbdomNative.Hook)](
        "insert" -> Snabbdom.hook { node =>
          val canvas = node.elm.get.asInstanceOf[Canvas]
          canvas.width = dimensions.width
          canvas.height = dimensions.height

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
