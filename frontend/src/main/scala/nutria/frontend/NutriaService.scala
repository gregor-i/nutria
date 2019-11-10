package nutria.frontend

import io.circe.syntax._
import io.circe.{Decoder, Encoder, parser}
import nutria.core.{FractalEntity, FractalEntityWithId, User}
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.Ajax.InputData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NutriaService {
  def whoAmI() : Future[Option[User]] =
    Ajax.get(url = s"/api/users/me")
      .flatMap(check(200))
      .flatMap(parse[Option[User]])

  def loadFractal(fractalId: String): Future[FractalEntity] =
    Ajax.get(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(parse[FractalEntity])

  def loadPublicFractals(): Future[Vector[FractalEntityWithId]] =
    Ajax.get(url = s"/api/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalEntityWithId]])

  def loadUserFractals(userId: String): Future[Vector[FractalEntityWithId]] =
    Ajax.get(url = s"/api/users/${userId}/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalEntityWithId]])

  def save(fractalEntity: FractalEntity): Future[FractalEntityWithId] =
    Ajax.post(
      url = s"/api/fractals",
      data = encode(fractalEntity)
    )
      .flatMap(check(201))
      .flatMap(parse[FractalEntityWithId])

  def deleteUserFractal(userId: String, fractalId: String): Future[Vector[FractalEntityWithId]] =
    Ajax.delete(url = s"/api/users/${userId}/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(_ => loadPublicFractals())

  def updateUserFractal(fractalEntity: FractalEntityWithId): Future[Unit] =
    Ajax.put(
      url = s"/api/users/${fractalEntity.owner.get}/fractals/${fractalEntity.id}",
      data = encode(fractalEntity)
    )
      .flatMap(check(202))
      .map(_ => ())

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
