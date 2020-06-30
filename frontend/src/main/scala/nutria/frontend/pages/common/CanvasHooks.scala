package nutria.frontend.pages.common

import nutria.core.FractalImage
import nutria.frontend.ExecutionContext
import nutria.frontend.util.AsyncUtil
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom.html.Canvas
import snabbdom.{Snabbdom, SnabbdomFacade}

object CanvasHooks extends ExecutionContext {
  def apply(fractal: FractalImage): Seq[(String, SnabbdomFacade.Hook)] =
    Seq[(String, SnabbdomFacade.Hook)](
      "insert" -> Snabbdom.hook { node =>
        val canvas           = node.elm.get.asInstanceOf[Canvas]
        val interactionPanel = canvas.parentElement
        AsyncUtil
          .sleep(0)
          .foreach { _ =>
            FractalRenderer.render(interactionPanel, canvas, fractal)
            canvas.style.transform = null
          }
      },
      "postpatch" -> Snabbdom.hook { (_, newNode) =>
        val canvas           = newNode.elm.get.asInstanceOf[Canvas]
        val interactionPanel = canvas.parentElement
        AsyncUtil
          .sleep(0)
          .foreach { _ =>
            FractalRenderer.render(interactionPanel, canvas, fractal)
            canvas.style.transform = null
          }
      },
      "destroy" -> Snabbdom.hook { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        canvas.getContext("webgl").getExtension("WEBGL_lose_context").loseContext()
      }
    )
}
