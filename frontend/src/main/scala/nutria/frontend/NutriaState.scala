package nutria.frontend

import io.circe.{Codec, Decoder, Encoder}
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

  def detailsState(fractalId: String): Future[DetailsState] =
    for{
      user <- NutriaService.whoAmI()
      fractal <- NutriaService.loadFractal(fractalId)
    } yield DetailsState(
      user = user,
      remoteFractal = fractal,
      fractal = fractal.entity
    )

  // todo: remove?
  implicit val encodeSaveProcess: Codec[Option[Future[FractalEntity]]] = Codec.from(
    decodeA = Decoder.decodeNone.map(none => none: Option[Future[FractalEntity]]),
    encodeA = Encoder.encodeNone.contramap(_ => None)
  )

  // todo: remove?
  implicit val encodeFuture: Codec[Future[NutriaState]] = Codec.from(
    decodeA = Decoder.decodeNone.map(_ => Future.failed(new Exception)),
    encodeA = Encoder.encodeNone.contramap(_ => None)
  )

  // todo: remove?
  implicit val codecLibraryState: Codec[LibraryState] = semiauto.deriveConfiguredCodec

  // todo: remove?
  implicit val codecExplorerState: Codec[ExplorerState] = semiauto.deriveConfiguredCodec

  // todo: remove?
  implicit val codec: Codec[NutriaState] = semiauto.deriveConfiguredCodec

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