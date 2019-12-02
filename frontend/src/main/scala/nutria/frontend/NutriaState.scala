package nutria.frontend

import io.circe.{Codec, Decoder, Encoder}
import monocle.Lens
import monocle.macros.GenLens
import nutria.core.{FractalEntity, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait NutriaState{
  def user: Option[User]
}

trait NoUser{ _: NutriaState =>
  def user: None.type = None
}

case class LoadingState(loading: Future[NutriaState]) extends NutriaState with NoUser

case class ErrorState(message: String) extends NutriaState with NoUser

case class GreetingState(randomFractal: FractalEntity) extends NutriaState with NoUser

case class ExplorerState(user: Option[User],
                         fractalId: Option[String],
                         fractalEntity: FractalEntity) extends NutriaState

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
  val viewport: Lens[ExplorerState, Viewport] = ExplorerState.fractalEntity.composeLens(FractalEntity.view)
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