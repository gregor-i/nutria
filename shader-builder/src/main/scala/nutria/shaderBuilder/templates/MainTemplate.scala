package nutria.shaderBuilder.templates

import nutria.core._
import nutria.shaderBuilder.{RefVec2, RefVec4}

object MainTemplate extends Template[FractalProgram] {
  override def constants(v: FractalProgram): Seq[String] =
    v match {
      case v: NewtonIteration                                           => NewtonIterationTemplate.constants(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[NormalMap]     => NormalMapTemplate.constants(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[OuterDistance] => OuterDistanceTemplate.constants(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[TimeEscape]    => TimeEscapeTemplate.constants(v)
      case v: FreestyleProgram                                          => FreestyleProgramTemplate.constants(v)
    }

  override def functions(v: FractalProgram): Seq[String] =
    v match {
      case v: NewtonIteration                                           => NewtonIterationTemplate.functions(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[NormalMap]     => NormalMapTemplate.functions(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[OuterDistance] => OuterDistanceTemplate.functions(v)
      case v: DivergingSeries if v.coloring.isInstanceOf[TimeEscape]    => TimeEscapeTemplate.functions(v)
      case v: FreestyleProgram                                          => FreestyleProgramTemplate.functions(v)
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
