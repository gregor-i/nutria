package nutria.frontend

import monocle.Lens
import monocle.macros.GenLens
import nutria.core._
import nutria.frontend.service.NutriaService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait NutriaState {
  def user: Option[User]
  def navbarExpanded: Boolean
}

trait NoUser {
  _: NutriaState =>
  def user: None.type = None
}

case class LoadingState(loading: Future[NutriaState], navbarExpanded: Boolean = false) extends NutriaState with NoUser

case class ErrorState(message: String, navbarExpanded: Boolean = false) extends NutriaState with NoUser

case class GreetingState(randomFractal: FractalImage, navbarExpanded: Boolean = false) extends NutriaState with NoUser

case class ExplorerState(
    user: Option[User],
    remoteFractal: Option[FractalEntityWithId],
    fractalImage: FractalImage,
    navbarExpanded: Boolean = false
) extends NutriaState

case class GalleryState(
    user: Option[User],
    publicFractals: Vector[FractalEntityWithId],
    votes: Map[String, VoteStatistic],
    navbarExpanded: Boolean = false
) extends NutriaState

case class UserGalleryState(
    user: Option[User],
    aboutUser: String,
    userFractals: Vector[FractalEntityWithId],
    navbarExpanded: Boolean = false
) extends NutriaState

case class DetailsState(
    user: Option[User],
    remoteFractal: FractalEntityWithId,
    fractalToEdit: FractalEntityWithId,
    navbarExpanded: Boolean = false
) extends NutriaState {
  def dirty: Boolean = remoteFractal != fractalToEdit
}

case class ProfileState(
    about: User,
    navbarExpanded: Boolean = false
) extends NutriaState {
  def user: Some[User] = Some(about)
}

case class AdminState(
    admin: User,
    users: Vector[User],
    fractals: Vector[FractalEntityWithId],
    navbarExpanded: Boolean = false
) extends NutriaState {
  def user: Some[User] = Some(admin)
}

object DetailsState {
  val remoteFractal: Lens[DetailsState, FractalEntityWithId] =
    GenLens[DetailsState](_.remoteFractal)
  val fractalToEdit: Lens[DetailsState, FractalEntityWithId] =
    GenLens[DetailsState](_.fractalToEdit)
}

object ExplorerState {
  val fractalImage: Lens[ExplorerState, FractalImage] = GenLens[ExplorerState](_.fractalImage)
  val viewport: Lens[ExplorerState, Viewport] =
    ExplorerState.fractalImage.composeLens(FractalImage.view)
}

object NutriaState extends CirceCodex {
  def setNavbarExtended(nutriaState: NutriaState, navbarExpanded: Boolean): NutriaState =
    nutriaState match {
      case state: LoadingState     => state.copy(navbarExpanded = navbarExpanded)
      case state: ErrorState       => state.copy(navbarExpanded = navbarExpanded)
      case state: GreetingState    => state.copy(navbarExpanded = navbarExpanded)
      case state: ExplorerState    => state.copy(navbarExpanded = navbarExpanded)
      case state: GalleryState     => state.copy(navbarExpanded = navbarExpanded)
      case state: UserGalleryState => state.copy(navbarExpanded = navbarExpanded)
      case state: DetailsState     => state.copy(navbarExpanded = navbarExpanded)
      case state: ProfileState     => state.copy(navbarExpanded = navbarExpanded)
      case state: AdminState       => state.copy(navbarExpanded = navbarExpanded)
    }
}
