package nutria.frontend.library

import monocle.Optional
import monocle.macros.GenLens
import nutria.core._

//@monocle.macros.Lenses()
case class LibraryState(programs: Vector[FractalEntity],
                        edit: Option[FractalEntity] = None)

object LibraryState {
  val editOptional: Optional[LibraryState, FractalEntity] = GenLens[LibraryState](_.edit)
    .composePrism(monocle.std.option.some)
}
