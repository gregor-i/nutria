package nutria.frontend.pages.common

import nutria.core.{Dimensions, FractalImage}
import nutria.frontend.util.Untyped
import nutria.shaderBuilder.FractalRenderer
import org.scalajs.dom
import org.scalajs.dom.html.{Canvas, Image}
import org.scalajs.dom.raw.WebGLRenderingContext
import snabbdom.{Node, Snabbdom}

import scala.scalajs.js.Dynamic

object FractalTile {
  def apply(fractalImage: FractalImage, dimensions: Dimensions): Node =
    ImgStrategy.render(fractalImage, dimensions)

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String =
    ImgStrategy.dataUrl(fractalImage, dimensions)
}

private object ImgStrategy {
  private lazy val canvas: Canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
  private lazy val webglCtx       = canvas.getContext("webgl").asInstanceOf[WebGLRenderingContext]

  private lazy val interval = dom.window.setInterval(
    () => {
      if (buffer.nonEmpty) {
        val task = buffer.dequeue()
        task.img.src = dataUrl(task.fractalImage, task.dimensions)
      }
    },
    100
  )

  private case class Task(img: Image, fractalImage: FractalImage, dimensions: Dimensions)

  private val buffer = scala.collection.mutable.Queue.empty[Task]

  def render(fractalImage: FractalImage, dimensions: Dimensions): Node =
    Node("img")
      .key(fractalImage.hashCode)
      .attr("width", dimensions.width.toString)
      .attr("height", dimensions.height.toString)
      .attr("src", "/assets/rendering.svg")
      .hook("insert", Snabbdom.hook { node =>
        val img = node.elm.get.asInstanceOf[Image]
        interval
        buffer.enqueue(Task(img, fractalImage, dimensions))
      })

  def dataUrl(fractalImage: FractalImage, dimensions: Dimensions): String = {
    canvas.width = dimensions.width
    canvas.height = dimensions.height
    FractalRenderer
      .compileProgram(webglCtx, fractalImage.template, fractalImage.antiAliase) match {
      case Right(webGlProgram) =>
        FractalRenderer.render(webglCtx, fractalImage.viewport, webGlProgram)
        canvas.toDataURL("image/png")
      case Left(_) => Images.compileError
    }
  }
}
