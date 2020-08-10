package nutria.frontend.pages.common

import nutria.core.{Dimensions, FractalImage}
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.{Node, Snabbdom, SnabbdomFacade}

import scala.scalajs.js
import scala.util.Random

object AnimatedFractalTile {
  def apply(fractalImageOverTime: LazyList[FractalImage], dimensions: Dimensions): Node =
    Node("canvas")
      .key(Random.nextInt())
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .style("backgroundImage", Images.rendering)
      .hooks(hooks(fractalImageOverTime))

  private def hooks(fractalImageOverTime: LazyList[FractalImage]): Seq[(String, SnabbdomFacade.Hook)] = {
    var animationHandle: Int = 0
    Seq(
      "insert" -> Snabbdom.hook { vnode =>
        val elem   = vnode.elm.get.asInstanceOf[Canvas]
        val ctx    = elem.getContext("webgl").asInstanceOf[WebGLRenderingContext]
        var frames = fractalImageOverTime

        def callback: js.Function1[Double, Unit] = _ => {
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
