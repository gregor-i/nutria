package nutria.frontend

import nutria.core.viewport.Dimensions
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Canvas

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Admin {
  def setup(): Unit = {
    Untyped(dom.window).putFractalImage = putFractalImage
    Untyped(dom.window).putAllFractalImages = putAllFractalImages
    Untyped(dom.window).cleanFractals = cleanFractals
    Untyped(dom.window).truncateImages = truncateImages
    Untyped(dom.window).insertSystemFractals = insertSystemFractals
    Untyped(dom.window).deleteFractal = deleteFractal
    println("Admin Setup completed")
  }

  val putFractalImage: String => Future[Unit] = (fractalId: String) =>
    for {
      fractal <- NutriaService.loadFractal(fractalId)
      canvas <- Future {
        val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
        canvas.setAttribute("width", Dimensions.thumbnailDimensions.width.toString)
        canvas.setAttribute("height", Dimensions.thumbnailDimensions.height.toString)
        canvas
      }
      _ = FractalRenderer.render(canvas, fractal, false)
      url = Untyped(canvas).toDataURL("image/png").asInstanceOf[String]
      _ <- Ajax.put(
        url = s"/api/fractals/${fractalId}/image",
        headers = Map("Content-Type" -> "image/png"),
        data = url.stripPrefix("data:image/png;base64,")
      )
      _ <- onFinished
    } yield ()

  val putAllFractalImages: Unit => Future[Unit] = _ =>
    for {
      fractals <- NutriaService.loadFractals()
      canvas <- Future {
        val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
        canvas.setAttribute("width", Dimensions.thumbnailDimensions.width.toString)
        canvas.setAttribute("height", Dimensions.thumbnailDimensions.height.toString)
        canvas
      }
      _ <- Future.sequence {
        for {
          fractal <- fractals
          _ = FractalRenderer.render(canvas, fractal.entity, false)
          url = Untyped(canvas).toDataURL("image/png").asInstanceOf[String]
        } yield Ajax.put(
          url = s"/api/fractals/${fractal.id}/image",
          headers = Map("Content-Type" -> "image/png"),
          data = url.stripPrefix("data:image/png;base64,")
        )
      }
      _ <- onFinished
    } yield ()

  val cleanFractals: Unit => Future[Unit] = _ =>
    Ajax.post(url = "/admin/clean-fractals")
      .flatMap(_ => onFinished)

  val truncateImages: Unit => Future[Unit] = _ =>
    Ajax.post(url = "/admin/truncate-images")
      .flatMap(_ => onFinished)

  val insertSystemFractals: Unit => Future[Unit] = _ =>
    Ajax.post(url = "/admin/insert-system-fractals")
      .flatMap(_ => onFinished)

  val deleteFractal: String => Future[Unit] = id =>
    Ajax.post(url = s"/admin/delete-fractal/$id")
      .flatMap(_ => onFinished)


  def onFinished = Future(dom.window.location.reload())
}
