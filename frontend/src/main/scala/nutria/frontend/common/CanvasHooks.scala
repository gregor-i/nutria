package nutria.frontend.common

import com.raquo.snabbdom.Modifier
import com.raquo.snabbdom.simple.{VNode, VNodeData}
import nutria.core.FractalEntity
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Hooks
import org.scalajs.dom.html.Canvas

object CanvasHooks {
  def apply(fractal: FractalEntity, resize: Boolean): Modifier[VNode, VNodeData] =
    Hooks { hooks =>
      hooks.addInsertHook { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, resize)
      }
      hooks.addPostPatchHook { (_, newNode) =>
        val canvas = newNode.elm.get.asInstanceOf[Canvas]
        FractalRenderer.render(canvas, fractal, resize)
      }
      hooks.addDestroyHook { node =>
        val canvas = node.elm.get.asInstanceOf[Canvas]
        canvas.getContext("webgl").getExtension("WEBGL_lose_context").loseContext()
      }
    }
}
