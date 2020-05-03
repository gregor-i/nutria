package nutria.shaderBuilder.templates

import nutria.core._
import nutria.shaderBuilder.{RefVec2, RefVec4}

object MainTemplate extends Template[FractalProgram] {
  override def definitions(v: FractalProgram): Seq[String] =
    v match {
      case v: NewtonIteration                                           => NewtonIterationTemplate.definitions(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[NormalMap]     => NormalMapTemplate.definitions(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[OuterDistance] => OuterDistanceTemplate.definitions(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[TimeEscape]    => TimeEscapeTemplate.definitions(v)
      case v: FreestyleProgram                                          => FreestyleProgramTemplate.definitions(v)
    }

  override def main(v: FractalProgram): String =
    v match {
      case v: NewtonIteration                                           => NewtonIterationTemplate.main(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[NormalMap]     => NormalMapTemplate.main(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[OuterDistance] => OuterDistanceTemplate.main(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[TimeEscape]    => TimeEscapeTemplate.main(v)
      case v: FreestyleProgram                                          => FreestyleProgramTemplate.main(v)
    }
}
