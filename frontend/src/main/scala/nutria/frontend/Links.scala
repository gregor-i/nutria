package nutria.frontend

import nutria.api.{FractalImageEntity, WithId}
import nutria.frontend.pages._
import nutria.frontend.service.FractalService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Links {
  def galleryState(page: Int = 1): Future[GalleryState] =
    for {
      publicFractals <- FractalService.listPublic()
    } yield GalleryState(publicFractals = publicFractals, page = page)

  def userGalleryState(userId: String, page: Int = 1): Future[UserGalleryState] =
    for {
      userFractals <- FractalService.loadUserFractals(userId)
    } yield UserGalleryState(aboutUser = userId, userFractals = userFractals, page = page)

  def greetingState(): Future[GreetingState] =
    for {
      randomFractal <- FractalService.getRandom()
    } yield GreetingState(randomFractal)

  def explorerState(fractal: WithId[FractalImageEntity]): ExplorerState =
    ExplorerState(
      remoteFractal = Some(fractal),
      fractalImage = fractal.entity
    )

  def explorerStateWithModal(fractal: WithId[FractalImageEntity]): ExplorerState =
    ExplorerState(remoteFractal = Some(fractal), fractalImage = fractal.entity, editModal = Some(()))
}
