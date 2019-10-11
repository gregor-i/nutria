package nutria.frontend.common

import nutria.core.FractalEntity
import nutria.frontend.shaderBuilder.FractalRenderer
import org.scalajs.dom.html.Canvas
import snabbdom.{Snabbdom, SnabbdomNative}

object CanvasHooks {
  def apply(fractal: FractalEntity, resize: Boolean): Seq[(String, SnabbdomNative.Hook)] =
    Seq[(String, SnabbdomNative.Hook)](
      "insert" -> Snabbdom.hook{ node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, resize)
      },
      "postpatch" -> Snabbdom.hook{(_, newNode) =>
        val canvas = newNode.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, resize)
      },
      "destroy" -> Snabbdom.hook{ node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        canvas.getContext("webgl").getExtension("WEBGL_lose_context").loseContext()
      }
    )
}
