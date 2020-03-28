package nutria.frontend

import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import monocle.Lens
import monocle.macros.{GenLens, Lenses}
import nutria.core._

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

@Lenses
case class ExplorerState(
    user: Option[User],
    remoteFractal: Option[FractalEntityWithId],
    fractalImage: FractalImage,
    saveModal: Option[SaveFractalDialog] = None,
    navbarExpanded: Boolean = false
) extends NutriaState

@Lenses
case class SaveFractalDialog(
    dimensions: Dimensions,
    antiAliase: Int Refined Positive
)

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

case class CreateNewFractalState(
    user: Option[User],
    fractal: Option[FractalEntity],
    navbarExpanded: Boolean = false
) extends NutriaState

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

case class FAQState(
    user: Option[User],
    navbarExpanded: Boolean = false
) extends NutriaState

object DetailsState {
  val remoteFractal: Lens[DetailsState, FractalEntityWithId] =
    GenLens[DetailsState](_.remoteFractal)
  val fractalToEdit: Lens[DetailsState, FractalEntityWithId] =
    GenLens[DetailsState](_.fractalToEdit)
}

object ExplorerState {
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalImage.composeLens(FractalImage.view)
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
      case state: FAQState         => state.copy(navbarExpanded = navbarExpanded)
    }
}
