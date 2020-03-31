package nutria.frontend.service

import io.circe.Decoder
import nutria.core.CirceCodex
import nutria.frontend.{AdminState, ErrorState, NutriaState}
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future

object NutriaAdminService extends Service with CirceCodex {
  private implicit val stateDecoder: Decoder[AdminState] =
    semiauto.deriveConfiguredDecoder[AdminState]

  def load(): Future[NutriaState] =
    Ajax
      .get("/api/admin")
      .flatMap(check(200))
      .flatMap(parse[AdminState])
      .recover { case error => ErrorState(error.getMessage) }

  def deleteUser(userId: String): Future[Unit] =
    Ajax
      .post(url = s"/api/admin/delete-user/$userId")
      .flatMap(check(200))
      .map(_ => ())

  def cleanFractals(): Future[Unit] =
    Ajax
      .post(url = "/api/admin/clean-fractals")
      .flatMap(check(200))
      .map(_ => ())

  def truncateFractals(): Future[Unit] =
    Ajax
      .post(url = "/api/admin/truncate-fractals")
      .flatMap(check(200))
      .map(_ => ())

  def insertSystemFractals(): Future[Unit] =
    Ajax
      .post(url = "/api/admin/insert-system-fractals")
      .flatMap(check(200))
      .map(_ => ())

  def deleteFractal(id: String): Future[Unit] =
    Ajax
      .post(url = s"/api/admin/delete-fractal/$id")
      .flatMap(check(200))
      .map(_ => ())
}
