package nutria.frontend

import nutria.core.{FractalEntityWithId, FractalImage, User}
import nutria.frontend.service.NutriaService

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

object Links {
  def galleryState(): Future[GalleryState] =
    for {
      user           <- NutriaService.whoAmI()
      publicFractals <- NutriaService.loadPublicFractals()
      votes          <- NutriaService.votes()
    } yield GalleryState(user = user, publicFractals = publicFractals, votes = votes)

  def userGalleryState(userId: String): Future[UserGalleryState] =
    for {
      user         <- NutriaService.whoAmI()
      userFractals <- NutriaService.loadUserFractals(userId)
    } yield UserGalleryState(user = user, aboutUser = userId, userFractals = userFractals)

  def greetingState(): Future[GreetingState] =
    for {
      randomFractal <- NutriaService.loadRandomFractal()
    } yield GreetingState(randomFractal)

  def detailsState(fractalId: String): Future[DetailsState] =
    for {
      user    <- NutriaService.whoAmI()
      fractal <- NutriaService.loadFractal(fractalId)
    } yield DetailsState(
      user = user,
      remoteFractal = fractal,
      fractalToEdit = fractal
    )

  def explorerState(fractal: FractalEntityWithId, user: Option[User]): ExplorerState =
    ExplorerState(
      user = user,
      remoteFractal = Some(fractal),
      fractalImage = FractalImage.firstImage(fractal.entity)
    )

  def detailsState(fractal: FractalEntityWithId, user: Option[User]): DetailsState =
    DetailsState(user = user, remoteFractal = fractal, fractalToEdit = fractal)
}
