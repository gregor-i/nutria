package nutria.frontend.library

import monocle.Optional
import monocle.macros.GenLens
import nutria.core._

//@monocle.macros.Lenses()
case class LibraryState(programs: Vector[FractalEntityWithId],
                        edit: Option[FractalEntityWithId] = None)

object LibraryState {
  val editOptional: Optional[LibraryState, FractalEntityWithId] = GenLens[LibraryState](_.edit)
    .composePrism(monocle.std.option.some)
}
