package nutria.frontend

import nutria.api.{FractalImageEntity, User, WithId}
import nutria.frontend.pages._
import nutria.frontend.service.NutriaService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Links {
  def galleryState(user: Option[User]): Future[GalleryState] =
    for {
      publicFractals <- NutriaService.loadPublicFractals()
      votes          <- NutriaService.votes()
    } yield GalleryState(user = user, publicFractals = publicFractals, votes = votes)

  def userGalleryState(user: Option[User], userId: String): Future[UserGalleryState] =
    for {
      userFractals <- NutriaService.loadUserFractals(userId)
    } yield UserGalleryState(user = user, aboutUser = userId, userFractals = userFractals)

  def greetingState(user: Option[User]): Future[GreetingState] =
    for {
      randomFractal <- NutriaService.loadRandomFractal()
    } yield GreetingState(randomFractal)

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
      fractalImage = fractal.entity.value
    )

  def detailsState(fractal: WithId[FractalImageEntity], user: Option[User]): DetailsState =
    DetailsState(user = user, remoteFractal = fractal, fractalToEdit = fractal)

  def faqState(user: Option[User]): FAQState =
    FAQState(user = user)
}
