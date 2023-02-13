package nutria.frontend.pages
package common

import nutria.core.FractalTemplate
import nutria.frontend.{CompileShaderException, FractalRenderer}
import org.scalajs.dom
import org.scalajs.dom.WebGLRenderingContext
import org.scalajs.dom.html.{Canvas, Element}
import snabbdom.Node

object CompileStatus {
  private lazy val canvas: Canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
  private lazy val webglCtx       = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

  private def hook(template: FractalTemplate)(elem: Element) =
    FractalRenderer.validateSource(template)(webglCtx) match {
      case Left(CompileShaderException(context, _, shader)) =>
        elem.classList.remove("is-success")
        elem.classList.add("is-danger")
        elem.innerHTML = s"<div class='message-body'>${context.getShaderInfoLog(shader).filter(_.toInt != 0)}</div>"
      case Right(_) =>
        elem.classList.add("is-success")
        elem.classList.remove("is-danger")
        elem.innerHTML = s"<div class='message-body'>Compiled successfully</div>"
    }

  def apply(template: FractalTemplate): Node = {
    "pre.is-paddingless.message"
      .prop("innerHTML", s"<div class='message-body'>Compiling ...</div>")
      .hookInsert { node =>
        hook(template)(node.elm.get)
      }
      .hookPostpatch { (_, newNode) =>
        hook(template)(newNode.elm.get)
      }
  }
}
