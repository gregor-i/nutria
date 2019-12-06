package nutria.frontend

import monocle.Lens
import monocle.macros.GenLens
import nutria.core.{FractalEntity, _}

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

case class LoadingState(loading: Future[NutriaState],
                        navbarExpanded: Boolean = false) extends NutriaState with NoUser

case class ErrorState(message: String,
                      navbarExpanded: Boolean = false) extends NutriaState with NoUser

case class GreetingState(randomFractal: FractalImage,
                         navbarExpanded: Boolean = false) extends NutriaState with NoUser

case class ExplorerState(user: Option[User],
                         fractalId: Option[String],
                         owned: Boolean,
                         fractalImage: FractalImage,
                         navbarExpanded: Boolean = false) extends NutriaState

case class LibraryState(user: Option[User],
                        publicFractals: Vector[FractalEntityWithId],
                        navbarExpanded: Boolean = false) extends NutriaState

case class DetailsState(user: Option[User],
                        remoteFractal: FractalEntityWithId,
                        fractal: FractalEntity,
                        navbarExpanded: Boolean = false) extends NutriaState{
  def dirty: Boolean = remoteFractal.entity != fractal
}

object DetailsState {
  val remoteFractal: Lens[DetailsState, FractalEntityWithId] = GenLens[DetailsState](_.remoteFractal)
  val fractalEntity: Lens[DetailsState, FractalEntity] = GenLens[DetailsState](_.fractal)
}

object ExplorerState {
  val fractalImage: Lens[ExplorerState, FractalImage] = GenLens[ExplorerState](_.fractalImage)
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalImage.composeLens(FractalImage.view)
}

object NutriaState extends CirceCodex {
  def libraryState(): Future[LibraryState] =
    for {
      user <- NutriaService.whoAmI()
      publicFractals <- NutriaService.loadPublicFractals()
    } yield LibraryState(user = user,
      publicFractals = publicFractals)

  def greetingState(): Future[GreetingState] =
    for {
      randomFractal <- NutriaService.loadRandomFractal()
    } yield GreetingState(randomFractal)

  def detailsState(fractalId: String): Future[DetailsState] =
    for{
      user <- NutriaService.whoAmI()
      fractal <- NutriaService.loadFractal(fractalId)
    } yield DetailsState(
      user = user,
      remoteFractal = fractal,
      fractal = fractal.entity
    )

  def setNavbarExtended(nutriaState: NutriaState, navbarExpanded: Boolean): NutriaState =
    nutriaState match {
      case state:LoadingState => state.copy(navbarExpanded = navbarExpanded)
      case state:ErrorState => state.copy(navbarExpanded = navbarExpanded)
      case state:GreetingState => state.copy(navbarExpanded = navbarExpanded)
      case state:ExplorerState => state.copy(navbarExpanded = navbarExpanded)
      case state:LibraryState => state.copy(navbarExpanded = navbarExpanded)
      case state:DetailsState => state.copy(navbarExpanded = navbarExpanded)
    }
}