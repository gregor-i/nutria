package nutria.frontend.pages.common

import nutria.core.FractalImage
import nutria.frontend.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.{Node, Snabbdom, SnabbdomFacade}

import scala.scalajs.js
import scala.util.Random

object AnimatedFractalTile {
  def apply(fractalImageOverTime: LazyList[FractalImage]): Node =
    Node("canvas")
      .key(Random.nextInt())
      .style("backgroundImage", Images.rendering)
      .event(
        "dblclick",
        Snabbdom.event { event =>
          val target = Untyped(event.target)
          if (dom.document.fullscreenElement != null) {
            dom.document.exitFullscreen()
          } else {
            target.requestFullscreen()
          }
        }
      )
      .hooks(hooks(fractalImageOverTime))

  private def hooks(fractalImageOverTime: LazyList[FractalImage]): Seq[(String, SnabbdomFacade.Hook)] = {
    var animationHandle: Int = 0
    Seq(
      "insert" -> Snabbdom.hook { vnode =>
        val canvas = vnode.elm.get.asInstanceOf[Canvas]
        val ctx    = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]
        var frames = fractalImageOverTime

        def callback: js.Function1[Double, Unit] = _ => {
          canvas.width = canvas.clientWidth
          canvas.height = canvas.clientHeight

          val fractalImage = frames.head
          frames = frames.drop(1)
          FractalRenderer.render(fractalImage)(ctx)
          animationHandle = dom.window.requestAnimationFrame(callback)
        }
        animationHandle = dom.window.requestAnimationFrame(callback)
      },
      "destroy" -> Snabbdom.hook { node =>
        dom.window.cancelAnimationFrame(animationHandle)
        node.elm.get
          .asInstanceOf[Canvas]
          .getContext("webgl")
          .getExtension("WEBGL_lose_context")
          .loseContext()
      }
    )
  }
}
