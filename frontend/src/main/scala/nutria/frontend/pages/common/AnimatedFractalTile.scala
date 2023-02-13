package nutria.frontend.pages
package common

import nutria.core.FractalImage
import nutria.frontend.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.WebGLRenderingContext
import org.scalajs.dom.html.Canvas
import snabbdom.{Event, Node}

import scala.scalajs.js
import scala.util.Random
import scala.util.chaining.scalaUtilChainingOps

object AnimatedFractalTile {
  def apply(fractalImageOverTime: LazyList[FractalImage]): Node =
    "canvas"
      .key(Random.nextInt())
      .style("backgroundImage", Images.rendering)
      .event[Event](
        "dblclick",
        event => {
          val target = event.target
          if (dom.document.fullscreenElement != null) {
            dom.document.exitFullscreen()
          } else {
            Untyped(target).requestFullscreen()
          }
        }
      )
      .pipe(addHooks(_)(fractalImageOverTime))

  private def addHooks(node: Node)(fractalImageOverTime: LazyList[FractalImage]): Node = {
    var animationHandle: Int = 0
    node
      .hookInsert { vnode =>
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
      }
      .hookDestroy { node =>
        dom.window.cancelAnimationFrame(animationHandle)
        node.elm.get
          .asInstanceOf[Canvas]
          .getContext("webgl")
          .getExtension("WEBGL_lose_context")
          .loseContext()
      }
  }
}
