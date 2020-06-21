package nutria.frontend

import nutria.api.{FractalImageEntity, User, WithId}
import nutria.frontend.pages._
import nutria.frontend.service.NutriaService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Links {
  def galleryState(user: Option[User], page: Int = 1): Future[GalleryState] =
    for {
      publicFractals <- NutriaService.loadPublicFractals()
    } yield GalleryState(user = user, publicFractals = publicFractals, page = page)

  def userGalleryState(user: Option[User], userId: String, page: Int = 1): Future[UserGalleryState] =
    for {
      userFractals <- NutriaService.loadUserFractals(userId)
    } yield UserGalleryState(user = user, aboutUser = userId, userFractals = userFractals, page = page)

  def greetingState(user: Option[User]): Future[GreetingState] =
    for {
      randomFractal <- NutriaService.loadRandomFractal()
    } yield GreetingState(user, randomFractal)

  def detailsState(user: Option[User], fractalId: String): Future[DetailsState] =
    for {
      fractal <- NutriaService.loadFractal(fractalId)
    } yield DetailsState(
      user = user,
      remoteFractal = fractal,
      fractalToEdit = fractal
    )

  def explorerState(fractal: WithId[FractalImageEntity], user: Option[User]): ExplorerState =
    ExplorerState(
      user = user,
      remoteFractal = Some(fractal),
      fractalImage = fractal.entity
    )

  def detailsState(fractal: WithId[FractalImageEntity], toEdit: FractalImageEntity, user: Option[User]): DetailsState =
    DetailsState(user = user, remoteFractal = fractal, fractalToEdit = fractal.copy(entity = toEdit))

  def detailsState(fractal: WithId[FractalImageEntity], user: Option[User]): DetailsState =
    DetailsState(user = user, remoteFractal = fractal, fractalToEdit = fractal)

  def faqState(user: Option[User]): FAQState =
    FAQState(user = user)
}
