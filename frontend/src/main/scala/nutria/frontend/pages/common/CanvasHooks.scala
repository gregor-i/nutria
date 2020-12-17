package nutria.frontend.pages
package common

import nutria.core.FractalImage
import nutria.frontend.util.AsyncUtil
import nutria.frontend.{ExecutionContext, FractalRenderer}
import org.scalajs.dom.html.Canvas
import snabbdom.Node

object CanvasHooks extends ExecutionContext {
  def apply(fractal: FractalImage)(node: Node): Node =
    node
      .hookInsert { node =>
        val canvas           = node.elm.get.asInstanceOf[Canvas]
        val interactionPanel = canvas.parentElement
        AsyncUtil
          .sleep(0)
          .foreach { _ =>
            FractalRenderer.render(interactionPanel, canvas, fractal)
            canvas.style.transform = null
          }
      }
      .hookPostpatch { (_, newNode) =>
        val canvas           = newNode.elm.get.asInstanceOf[Canvas]
        val interactionPanel = canvas.parentElement
        AsyncUtil
          .sleep(0)
          .foreach { _ =>
            FractalRenderer.render(interactionPanel, canvas, fractal)
            canvas.style.transform = null
          }
      }
      .hookDestroy { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        canvas.getContext("webgl").getExtension("WEBGL_lose_context").loseContext()
      }
}
