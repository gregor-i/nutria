package nutria.frontend.service

import nutria.api.{User, Verdict, VoteStatistic, WithId}
import nutria.core._

import scala.concurrent.Future

object NutriaService extends Service {
  def whoAmI(): Future[Option[User]] =
    get("/api/users/me")
      .flatMap(check(200))
      .flatMap(parse[Option[User]])

  def loadFractal(fractalId: String): Future[WithId[FractalEntity]] =
    get(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(parse[WithId[FractalEntity]])

  def loadPublicFractals(): Future[Vector[WithId[FractalEntity]]] =
    get(url = "/api/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[WithId[FractalEntity]]])

  def loadRandomFractal(): Future[FractalImage] =
    get(url = "/api/fractals/random")
      .flatMap(check(200))
      .flatMap(parse[FractalImage])

  def loadUserFractals(userId: String): Future[Vector[WithId[FractalEntity]]] =
    get(url = s"/api/users/${userId}/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[WithId[FractalEntity]]])

  def save(fractalEntity: FractalEntity): Future[WithId[FractalEntity]] =
    post("/api/fractals", fractalEntity)
      .flatMap(check(201))
      .flatMap(parse[WithId[FractalEntity]])

  def deleteFractal(fractalId: String): Future[Unit] =
    delete(s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .map(_ => ())

  def updateFractal(fractalEntity: WithId[FractalEntity]): Future[Unit] =
    put(s"/api/fractals/${fractalEntity.id}", fractalEntity.entity)
      .flatMap(check(202))
      .map(_ => ())

  def deleteUser(userId: String): Future[Unit] =
    delete(s"/api/users/${userId}")
      .flatMap(check(204))
      .map(_ => ())

  def votes(): Future[Map[String, VoteStatistic]] =
    get("/api/votes")
      .flatMap(check(200))
      .flatMap(parse[Map[String, VoteStatistic]])

  def vote(fractalId: String, verdict: Verdict): Future[Unit] =
    put(s"/api/votes/${fractalId}", verdict)
      .flatMap(check(204))
      .map(_ => ())

  def deleteVote(fractalId: String): Future[Unit] =
    delete(s"/api/votes/${fractalId}")
      .flatMap(check(204))
      .map(_ => ())
}
