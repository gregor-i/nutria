package nutria.frontend

import io.circe.{Codec, Decoder, Encoder}
import monocle.macros.GenLens
import monocle.{Lens, Optional}
import nutria.core.{FractalEntity, _}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait NutriaState{
  def user: Option[User]
}

case class LoadingState(loading: Future[NutriaState]) extends NutriaState {
  def user: None.type = None
}

case class ErrorState(user: Option[User], message: String) extends NutriaState

case class ExplorerState(user: Option[User],
                         fractalEntity: FractalEntity,
                         edit: Option[FractalEntity] = None,
                         tab: Tab = Tab.default,
                         saveProcess: Option[Future[FractalEntity]] = None) extends NutriaState

case class LibraryState(user: Option[User],
                        publicFractals: Vector[FractalEntityWithId]) extends NutriaState

case class DetailsState(user: Option[User],
                        remoteFractal: FractalEntityWithId,
                        fractal: FractalEntity) extends NutriaState

object DetailsState {
  val remoteFractal: Lens[DetailsState, FractalEntityWithId] = GenLens[DetailsState](_.remoteFractal)
  val fractalEntity: Lens[DetailsState, FractalEntity] = GenLens[DetailsState](_.fractal)
}

object ExplorerState {
  val fractalEntity: Lens[ExplorerState, FractalEntity] = GenLens[ExplorerState](_.fractalEntity)
  val edit: Lens[ExplorerState, Option[FractalEntity]] = GenLens[ExplorerState](_.edit)
  val editOptional: Optional[ExplorerState, FractalEntity] = edit.composePrism(monocle.std.option.some)
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalEntity.composeLens(FractalEntity.view)
  val tab: Lens[ExplorerState, Tab] = GenLens[ExplorerState](_.tab)
}

object NutriaState extends CirceCodex {
  def libraryState(): Future[LibraryState] =
    for {
      user <- NutriaService.whoAmI()
      publicFractals <- NutriaService.loadPublicFractals()
    } yield LibraryState(user = user,
      publicFractals = publicFractals)


  implicit val encodeSaveProcess: Codec[Option[Future[FractalEntity]]] = Codec.from(
    decodeA = Decoder.decodeNone.map(none => none: Option[Future[FractalEntity]]),
    encodeA = Encoder.encodeNone.contramap(_ => None)
  )

  implicit val encodeFuture: Codec[Future[NutriaState]] = Codec.from(
    decodeA = Decoder.decodeNone.map(_ => Future.failed(new Exception)),
    encodeA = Encoder.encodeNone.contramap(_ => None)
  )

  implicit val codecLibraryState: Codec[LibraryState] = semiauto.deriveConfiguredCodec

  implicit val codecExplorerState: Codec[ExplorerState] = semiauto.deriveConfiguredCodec

  implicit val codec: Codec[NutriaState] = semiauto.deriveConfiguredCodec
}