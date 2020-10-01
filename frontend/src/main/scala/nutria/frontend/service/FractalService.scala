package nutria.frontend.service

import nutria.api.{FractalImageEntity, WithId}
import nutria.core.FractalImage
import nutria.frontend.service.Service._

import scala.concurrent.Future

object FractalService {
  def get(fractalId: String): Future[WithId[FractalImageEntity]] =
    Service
      .get(url = s"/api/fractals/${fractalId}")
      .flatMap(check(200))
      .flatMap(parse[WithId[FractalImageEntity]])

  def listPublic(): Future[Vector[WithId[FractalImageEntity]]] =
    Service
      .get(url = "/api/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[WithId[FractalImageEntity]]])

  def getRandom(): Future[FractalImage] =
    Service
      .get(url = "/api/fractals/random")
      .flatMap(check(200))
      .flatMap(parse[FractalImage])

  def loadUserFractals(userId: String): Future[Vector[WithId[FractalImageEntity]]] =
    Service
      .get(url = s"/api/users/${userId}/fractals")
      .flatMap(check(200))
      .flatMap(parse[Vector[WithId[FractalImageEntity]]])

  def post(fractalEntity: FractalImageEntity): Future[WithId[FractalImageEntity]] =
    Service
      .post("/api/fractals", fractalEntity)
      .flatMap(check(201))
      .flatMap(parse[WithId[FractalImageEntity]])

  def delete(fractalId: String): Future[Unit] =
    Service
      .delete(s"/api/fractals/${fractalId}")
      .flatMap(check(204))
      .map(_ => ())

  def put(fractalEntity: WithId[FractalImageEntity]): Future[WithId[FractalImageEntity]] =
    Service
      .put(s"/api/fractals/${fractalEntity.id}", fractalEntity.entity)
      .flatMap(check(200))
      .flatMap(parse[WithId[FractalImageEntity]])

}
