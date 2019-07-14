package nutria.frontend.viewer

import monocle.{Lens, Optional}
import monocle.macros.GenLens
import nutria.core.{FractalEntity, FractalProgram}

case class ViewerState(fractalEntity: FractalEntity,
                       edit: Option[FractalEntity] = None,
                       dragStartPosition: Option[(Double, Double)] = None)

object ViewerState {
  val fractalEntity: Lens[ViewerState, FractalEntity] = GenLens[ViewerState](_.fractalEntity)
  val edit: Lens[ViewerState, Option[FractalEntity]] = GenLens[ViewerState](_.edit)
  val editOptional: Optional[ViewerState, FractalEntity] = edit.composePrism(monocle.std.option.some)

  val viewport = ViewerState.fractalEntity
    .composeLens(FractalEntity.program)
    .composeLens(FractalProgram.viewport)
}