package nutria.frontend.viewer

import monocle.macros.GenLens
import monocle.{Lens, Optional}
import nutria.core.{FractalEntity, FractalProgram}

import scala.concurrent.Future

case class ViewerState(fractalEntity: FractalEntity,
                       edit: Option[FractalEntity] = None,
                       dragStartPosition: Option[(Double, Double)] = None,
                       saveProcess: Option[Future[FractalEntity]] = None)

object ViewerState {
  val fractalEntity: Lens[ViewerState, FractalEntity] = GenLens[ViewerState](_.fractalEntity)
  val edit: Lens[ViewerState, Option[FractalEntity]] = GenLens[ViewerState](_.edit)
  val editOptional: Optional[ViewerState, FractalEntity] = edit.composePrism(monocle.std.option.some)

  val viewport = ViewerState.fractalEntity
    .composeLens(FractalEntity.view)
}
