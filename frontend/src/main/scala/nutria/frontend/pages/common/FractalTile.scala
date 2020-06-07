package nutria.frontend.pages.common

import nutria.core.{Dimensions, FractalImage}
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FractalTile {
  def apply(fractalImage: FractalImage, dimensions: Dimensions): Node =
    ImgStrategy.render(fractalImage, dimensions)

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String =
    ImgStrategy.dataUrl(fractalImage, dimensions)
}

private object ImgStrategy {
  private lazy val canvas: Canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
  private lazy val webglCtx       = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

  def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("img")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .attr("src", Images.rendering)
      .hook(
        "insert",
        Snabbdom.hook { node =>
          val img = node.elm.get.asInstanceOf[Image]
          Future(dataUrl(fractalImage, dimensions))
            .foreach(dataUrl => img.src = dataUrl)
        }
      )

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String = {
    canvas.width = dimensions.width
    canvas.height = dimensions.height
    FractalRenderer.render(fractalImage)(webglCtx) match {
      case Right(()) => canvas.toDataURL("image/png")
      case Left(_)   => Images.compileError
    }
  }
}
