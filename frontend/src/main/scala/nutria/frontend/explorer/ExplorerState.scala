package nutria.frontend.explorer

import monocle.macros.GenLens
import monocle.{Lens, Optional}
import nutria.core.{FractalEntity, FractalProgram}

import scala.concurrent.Future

case class ExplorerState(fractalEntity: FractalEntity,
                         edit: Option[FractalEntity] = None,
                         dragStartPosition: Option[(Double, Double)] = None,
                         saveProcess: Option[Future[FractalEntity]] = None)

object ExplorerState {
  val fractalEntity: Lens[ExplorerState, FractalEntity] = GenLens[ExplorerState](_.fractalEntity)
  val edit: Lens[ExplorerState, Option[FractalEntity]] = GenLens[ExplorerState](_.edit)
  val editOptional: Optional[ExplorerState, FractalEntity] = edit.composePrism(monocle.std.option.some)

  val viewport = ExplorerState.fractalEntity
    .composeLens(FractalEntity.view)
}
