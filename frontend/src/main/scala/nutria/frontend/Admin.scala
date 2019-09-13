package nutria.frontend

import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Canvas
import scala.concurrent.ExecutionContext.Implicits.global


import scala.concurrent.Future

object Admin {
  def setup(): Unit ={
    Untyped(dom.window).putFractalImage = (fractalId: String) => {
      (for{
        fractal  <- NutriaService.loadFractal(fractalId)
        canvas  <- Future{
          val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
          canvas.setAttribute("width", "400")
          canvas.setAttribute("height", "225")
          canvas
        }
        _ = FractalRenderer.render(canvas, fractal, false)
        url = Untyped(canvas).toDataURL("image/png").asInstanceOf[String]
        post <- Ajax.put(
          url = s"/api/fractals/${fractalId}/image",
          headers = Map("Content-Type" -> "image/png"),
          data = url.stripPrefix("data:image/png;base64,")
        )
      } yield post.responseText)
        .onComplete(println)
    }
    println("Admin Setup completed")
  }
}
