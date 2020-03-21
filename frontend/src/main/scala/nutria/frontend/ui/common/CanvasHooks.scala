package nutria.frontend.ui.common

import nutria.core.FractalImage
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom.html.Canvas
import snabbdom.{Snabbdom, SnabbdomFacade}

object CanvasHooks {
  def apply(fractal: FractalImage, resize: Boolean): Seq[(String, SnabbdomFacade.Hook)] =
    Seq[(String, SnabbdomFacade.Hook)](
      "insert" -> Snabbdom.hook { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, resize)
      },
      "postpatch" -> Snabbdom.hook { (_, newNode) =>
        val canvas = newNode.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, resize)
      },
      "destroy" -> Snabbdom.hook { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        canvas.getContext("webgl").getExtension("WEBGL_lose_context").loseContext()
      }
    )
}
