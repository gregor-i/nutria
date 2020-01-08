package nutria.frontend.service

import nutria.core.{FractalEntity, FractalEntityWithId, FractalImage, User, Verdict, VoteStatistic}
import org.scalajs.dom.ext.Ajax

import scala.concurrent.Future

object NutriaService extends Service {
  def whoAmI(): Future[Option[User]] =
    Ajax
      .get(url = s"/api/users/me")
      .flatMap(check(200))
      .flatMap(parse[Option[User]])

  def loadFractal(fractalId: String): Future[FractalEntityWithId] =
    Ajax
      .get(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(parse[FractalEntityWithId])

  def loadPublicFractals(): Future[Vector[FractalEntityWithId]] =
    Ajax
      .get(url = s"/api/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalEntityWithId]])

  def loadRandomFractal(): Future[FractalImage] =
    Ajax
      .get(url = "/api/fractals/random")
      .flatMap(check(200))
      .flatMap(parse[FractalImage])

  def loadUserFractals(userId: String): Future[Vector[FractalEntityWithId]] =
    Ajax
      .get(url = s"/api/users/${userId}/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[FractalEntityWithId]])

  def save(fractalEntity: FractalEntity): Future[FractalEntityWithId] =
    Ajax
      .post(
        url = s"/api/fractals",
        data = encode(fractalEntity)
      )
      .flatMap(check(201))
      .flatMap(parse[FractalEntityWithId])

  def deleteFractal(fractalId: String): Future[Vector[FractalEntityWithId]] =
    Ajax
      .delete(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(_ => loadPublicFractals())

  def updateFractal(fractalEntity: FractalEntityWithId): Future[Unit] =
    Ajax
      .put(
        url = s"/api/fractals/${fractalEntity.id}",
        data = encode(fractalEntity.entity)
      )
      .flatMap(check(202))
      .map(_ => ())

  def deleteUser(userId: String): Future[Unit] =
    Ajax
      .delete(s"/api/users/${userId}")
      .flatMap(check(204))
      .map(_ => ())

  def votes(): Future[Map[String, VoteStatistic]] =
    Ajax
      .get(s"/api/votes")
      .flatMap(check(200))
      .flatMap(parse[Map[String, VoteStatistic]])

  def vote(fractalId: String, verdict: Verdict): Future[Unit] =
    Ajax
      .put(url = s"/api/votes/${fractalId}", data = encode(verdict))
      .flatMap(check(204))
      .map(_ => ())

  def deleteVote(fractalId: String): Future[Unit] =
    Ajax
      .delete(url = s"/api/votes/${fractalId}")
      .flatMap(check(204))
      .map(_ => ())
}
