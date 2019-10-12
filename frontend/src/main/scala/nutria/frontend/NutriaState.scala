package nutria.frontend

import io.circe.{Codec, Decoder, Encoder}
import monocle.macros.GenLens
import monocle.{Lens, Optional}
import nutria.core.{FractalEntity, _}

import scala.concurrent.Future

sealed trait NutriaState

//case class LoadingState(loading: Future[Vector[FractalEntityWithId]]) extends State

case class ErrorState(message: String) extends NutriaState

case class ExplorerState(fractalEntity: FractalEntity,
                         edit: Option[FractalEntity] = None,
                         saveProcess: Option[Future[FractalEntity]] = None) extends NutriaState

case class LibraryState(fractals: Vector[FractalEntityWithId],
                        edit: Option[FractalEntityWithId] = None) extends NutriaState


object ExplorerState {
  val fractalEntity: Lens[ExplorerState, FractalEntity] = GenLens[ExplorerState](_.fractalEntity)
  val edit: Lens[ExplorerState, Option[FractalEntity]] = GenLens[ExplorerState](_.edit)
  val editOptional: Optional[ExplorerState, FractalEntity] = edit.composePrism(monocle.std.option.some)
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalEntity.composeLens(FractalEntity.view)
}

object LibraryState {
  val editOptional: Optional[LibraryState, FractalEntityWithId] = GenLens[LibraryState](_.edit).composePrism(monocle.std.option.some)
}

object NutriaState extends CirceCodex {
  implicit val encodeSaveProcess: Codec[Option[Future[FractalEntity]]] = Codec.from(
    decodeA = Decoder.decodeNone.map(none => none: Option[Future[FractalEntity]]),
    encodeA = Encoder.encodeNone.contramap(_ => None)
  )

  implicit val codecLibraryState: Codec[LibraryState] = semiauto.deriveConfiguredCodec

  implicit val codecExplorerState: Codec[ExplorerState] = semiauto.deriveConfiguredCodec

  implicit val codec: Codec[NutriaState] = semiauto.deriveConfiguredCodec
}