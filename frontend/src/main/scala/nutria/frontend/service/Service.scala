package nutria.frontend.service

import io.circe.syntax._
import io.circe.{Decoder, Encoder, parser}
import org.scalajs.dom.experimental.{Fetch, HttpMethod, RequestInit, Response}

import scala.concurrent.{ExecutionContext, Future}

trait Service {
  implicit val ex: ExecutionContext = ExecutionContext.global

  def get(url: String): Future[Response] = Fetch.fetch(url).toFuture

  def post(url: String): Future[Response] =
    Fetch.fetch(url, RequestInit(method = HttpMethod.POST)).toFuture

  def delete(url: String): Future[Response] =
    Fetch.fetch(url, RequestInit(method = HttpMethod.DELETE)).toFuture

  def post[A: Encoder](url: String, body: A): Future[Response] =
    Fetch.fetch(url, RequestInit(method = HttpMethod.POST, body = body.asJson.noSpaces)).toFuture

  def put[A: Encoder](url: String, body: A): Future[Response] =
    Fetch.fetch(url, RequestInit(method = HttpMethod.PUT, body = body.asJson.noSpaces)).toFuture

  def check(excepted: Int)(req: Response): Future[Response] =
    if (req.status == excepted)
      Future.successful(req)
    else
      Future.failed(new Exception(s"unexpected response code: ${req.status}"))

  def parse[A: Decoder](req: Response): Future[A] =
    req
      .text()
      .toFuture
      .map(parser.decode[A])
      .map(_.toTry)
      .flatMap(Future.fromTry)

  def toUnit(fut: Future[_]): Future[Unit] = fut.map(_ => ())
}
