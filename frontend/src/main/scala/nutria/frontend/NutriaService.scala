package nutria.frontend

import io.circe.syntax._
import io.circe.{Decoder, Encoder, parser}
import nutria.core.FractalEntity
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NutriaService {
  def loadFractals(): Future[Vector[FractalEntity]] =
    Ajax.get(url = s"/api/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalEntity]])

  def save(fractalEntity: FractalEntity): Future[Vector[FractalEntity]] =
    Ajax.post(
      url = s"/api/fractals",
      data = encode(fractalEntity)
    )
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalEntity]])

  private def check(excepted: Int)(req: XMLHttpRequest): Future[XMLHttpRequest] =
    if (req.status == excepted)
      Future.successful(req)
    else
      Future.failed(new Exception(s"unexpected response code: ${req.status}"))

  private def parse[A: Decoder](req: XMLHttpRequest): Future[A] =
    Future(parser.decode[A](req.responseText).right.get)

  private def encode[A: Encoder](a: A): InputData =
    a.asJson.noSpaces.asInstanceOf[InputData]
}
