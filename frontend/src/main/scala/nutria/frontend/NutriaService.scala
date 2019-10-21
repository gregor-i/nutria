package nutria.frontend

import io.circe.syntax._
import io.circe.{Decoder, Encoder, parser}
import nutria.core.viewport.Dimensions
import nutria.core.{FractalEntity, FractalEntityWithId}
import nutria.frontend.shaderBuilder.FractalRenderer
import nutria.frontend.util.Untyped
import org.scalajs.dom
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData
import org.scalajs.dom.html.Canvas

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NutriaService {
  def loadFractal(fractalId: String): Future[FractalEntity] =
    Ajax.get(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(parse[FractalEntity])

  def loadFractals(): Future[Vector[FractalEntityWithId]] =
    Ajax.get(url = s"/api/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalEntityWithId]])

  def save(fractalEntity: FractalEntity): Future[FractalEntityWithId] =
    Ajax.post(
      url = s"/api/fractals",
      data = encode(fractalEntity)
    )
      .flatMap(check(201))
      .flatMap(parse[FractalEntityWithId])

  def saveImage(fractal: FractalEntityWithId): Future[Unit] =
    for {
      canvas <- Future {
        val canvas = dom.document.createElement("canvas").asInstanceOf[Canvas]
        canvas.setAttribute("width", Dimensions.thumbnailDimensions.width.toString)
        canvas.setAttribute("height", Dimensions.thumbnailDimensions.height.toString)
        canvas
      }
      _ = FractalRenderer.render(canvas, fractal.entity, false)
      url = Untyped(canvas).toDataURL("image/png").asInstanceOf[String]
      _ <- Ajax.put(
        url = s"/api/fractals/${fractal.id}/image",
        headers = Map("Content-Type" -> "image/png"),
        data = url.stripPrefix("data:image/png;base64,")
      ).flatMap(check(200))
    } yield ()

  def delete(fractalId: String): Future[Vector[FractalEntityWithId]] =
    Ajax.delete(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(_ => loadFractals())

  private def check(excepted: Int)(req: XMLHttpRequest): Future[XMLHttpRequest] =
    if (req.status == excepted)
      Future.successful(req)
    else
      Future.failed(new Exception(s"unexpected response code: ${req.status}"))

  private def parse[A: Decoder](req: XMLHttpRequest): Future[A] =
    Future.fromTry(parser.decode[A](req.responseText).toTry)

  private def encode[A: Encoder](a: A): InputData =
    a.asJson.noSpaces.asInstanceOf[InputData]
}
