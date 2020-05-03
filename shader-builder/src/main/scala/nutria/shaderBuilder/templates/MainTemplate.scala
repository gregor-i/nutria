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

  override def main(v: FractalProgram)(inputVar: RefVec2, outputVar: RefVec4): String =
    v match {
      case v: NewtonIteration                                           => NewtonIterationTemplate.main(v)(inputVar, outputVar)
      case v: DivergingSeries if v.coloring.isInstanceOf[NormalMap]     => NormalMapTemplate.main(v)(inputVar, outputVar)
      case v: DivergingSeries if v.coloring.isInstanceOf[OuterDistance] => OuterDistanceTemplate.main(v)(inputVar, outputVar)
      case v: DivergingSeries if v.coloring.isInstanceOf[TimeEscape]    => TimeEscapeTemplate.main(v)(inputVar, outputVar)
      case v: FreestyleProgram                                          => FreestyleProgramTemplate.main(v)(inputVar, outputVar)
    }
}
