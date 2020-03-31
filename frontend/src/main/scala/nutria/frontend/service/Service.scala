package nutria.frontend.service

import io.circe.syntax._
import io.circe.{Decoder, Encoder, parser}
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.{ExecutionContext, Future}

trait Service {
  implicit val ex: ExecutionContext = ExecutionContext.global

  def check(excepted: Int)(req: XMLHttpRequest): Future[XMLHttpRequest] =
    if (req.status == excepted)
      Future.successful(req)
    else
      Future.failed(new Exception(s"unexpected response code: ${req.status}"))

  def parse[A: Decoder](req: XMLHttpRequest): Future[A] =
    Future.fromTry(parser.decode[A](req.responseText).toTry)

  def encode[A: Encoder](a: A): InputData =
    a.asJson.noSpaces.asInstanceOf[InputData]

  def toUnit(fut: Future[_]): Future[Unit] = fut.map(_ => ())
}
