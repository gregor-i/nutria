package nutria.frontend.util

import mathParser.complex.Complex
import monocle.Lens
import monocle.macros.GenLens

object ComplexLenses {
  val real: Lens[Complex, Double] = GenLens[Complex](_.real)
  val imag: Lens[Complex, Double] = GenLens[Complex](_.imag)
}
