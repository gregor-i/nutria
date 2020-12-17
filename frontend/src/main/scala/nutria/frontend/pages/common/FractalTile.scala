package nutria.frontend.pages.common

import nutria.core.{Dimensions, FractalImage}
import nutria.frontend.FractalRenderer
import nutria.frontend.util.AsyncUtil
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

object FractalTile {
  def apply(fractalImage: FractalImage, dimensions: Dimensions): Node =
    ImgStrategy.render(fractalImage, dimensions)

  // todo: move to renderer?
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
      .hookInsert { node =>
        AsyncUtil
          .sleep(0)
          .foreach { _ =>
            node.elm.get.asInstanceOf[Image].src = dataUrl(fractalImage, dimensions)
          }
      }

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String = {
    canvas.width = dimensions.width
    canvas.height = dimensions.height
    FractalRenderer.render(fractalImage)(webglCtx) match {
      case Right(_) => canvas.toDataURL("image/png")
      case Left(_)  => Images.compileError
    }
  }
}

private object CanvasStrategy {
  def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("canvas")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .style("backgroundImage", Images.rendering)
      .hookInsert { node =>
        AsyncUtil
          .sleep(1)
          .foreach { _ =>
            val canvas = node.elm.get.asInstanceOf[Canvas]
            withOffscreenCanvas(canvas.width, canvas.height) { (offScreenCanvas, gl) =>
              FractalRenderer.render(fractalImage)(gl) match {
                case Right(_) =>
                  val canvasContext = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
                  canvasContext.drawImage(offScreenCanvas, 0, 0)
                  canvas.style.backgroundImage = null
                case Left(_) =>
                  canvas.style.backgroundImage = Images.compileError
              }
            }
          }
      }

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String = {
    withOffscreenCanvas(dimensions.width, dimensions.height) { (offScreenCanvas, gl) =>
      FractalRenderer.render(fractalImage)(gl) match {
        case Right(_) => offScreenCanvas.toDataURL("image/png")
        case Left(_)  => Images.compileError
      }
    }
  }

  private def withOffscreenCanvas[A](width: Int, height: Int)(op: (Canvas, WebGLRenderingContext) => A): A = {
    val offscreenCanvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
    offscreenCanvas.width = width
    offscreenCanvas.height = height
    val gl = offscreenCanvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

    try {
      op(offscreenCanvas, gl)
    } finally {
      gl.getExtension("WEBGL_lose_context").asInstanceOf[js.Dynamic].loseContext()
    }
  }
}
