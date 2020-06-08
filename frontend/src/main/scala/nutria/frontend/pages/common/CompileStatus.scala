package nutria.frontend.pages.common

import nutria.core.{AntiAliase, FractalTemplate}
import nutria.shaderBuilder.{CompileShaderException, FractalRenderer}
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Element}
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.{Node, Snabbdom}

object CompileStatus {
  private lazy val canvas: Canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
  private lazy val webglCtx       = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

  private def hook(template: FractalTemplate, aa: AntiAliase)(elem: Element) =
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

  def apply(template: FractalTemplate, aa: AntiAliase = 1): Node = {
    Node("pre.is-paddingless.message")
      .prop("innerHTML", s"<div class='message-body'>Compiling ...</div>")
      .hook(
        "insert",
        Snabbdom.hook { node =>
          hook(template, aa)(node.elm.get)
        }
      )
      .hook(
        "postpatch",
        Snabbdom.hook { (_, newNode) =>
          hook(template, aa)(newNode.elm.get)
        }
      )
  }
}
