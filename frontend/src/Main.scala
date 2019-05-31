import nutria.core.content.LinearNormalized
import nutria.data.Defaults
import org.scalajs.dom
import org.scalajs.dom.{CanvasRenderingContext2D, Event}
import org.scalajs.dom.html.Canvas
import nutria.core.syntax._
import nutria.data.colors.Wikipedia
import nutria.data.consumers.CountIterations
import nutria.data.sequences.Mandelbrot

object Main {
  val dim = Defaults.defaultDimensions.scale(0.25)

  def start():Unit = {
    val canvas = dom.document.getElementById("nutria-canvas").asInstanceOf[Canvas]
    val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    canvas.height = dim.height
    canvas.width = dim.width

    val img = Defaults.defaultViewport
      .withDimensions(dim)
      .withContent(Mandelbrot.apply(50) andThen CountIterations.smoothed() andThen LinearNormalized.apply(0, 50) andThen Wikipedia)

    for{
      x <- 0 until dim.width
      y <- 0 until dim.height
    } {
      val c = img(x, y)
      ctx.fillStyle = c.toString
      ctx.fillRect(x, y, 1, 1)
    }
  }



  def main(args: Array[String]): Unit = {
    dom.document.addEventListener[Event]("DOMContentLoaded", (_: Event) => start())
  }
}
