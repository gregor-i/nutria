package nutria.data

import nutria.core.{DerivedDivergingSeries, Dimensions, DivergingSeries, FractalProgram, NewtonIteration, Point, newton}
import nutria.data.colors.RGBA
import nutria.data.consumers.NewtonColoring
import nutria.data.sequences.NewtonFractalByString

object FractalProgramToContent {
  def apply(program: FractalProgram): Point => RGBA = program match {
    case series: DivergingSeries => ???
    case _: DerivedDivergingSeries => ???
    case newton: NewtonIteration =>
      val f = NewtonFractalByString(newton.function, newton.initial)
      f(newton.maxIterations, newton.threshold, newton.overshoot)
        .andThen(NewtonColoring.smooth(f))
  }
}
