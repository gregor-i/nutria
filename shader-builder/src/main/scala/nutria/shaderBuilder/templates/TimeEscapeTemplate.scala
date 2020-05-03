package nutria.shaderBuilder.templates

import mathParser.Syntax._
import nutria.core.{DivergingSeries, TimeEscape}
import nutria.shaderBuilder._

private[templates] object TimeEscapeTemplate extends Template[DivergingSeries] {
  override def definitions(v: DivergingSeries): Seq[String] = {
    val c = v.coloring.asInstanceOf[TimeEscape]
    Seq(
      constant("max_iterations", IntLiteral(v.maxIterations.value)),
      constant("escape_radius", FloatLiteral(v.escapeRadius.value)),
      constant("color_inside", Vec4.fromRGBA(c.colorInside)),
      constant("color_outside", Vec4.fromRGBA(c.colorOutside)),
      function("initial", v.initial.node.optimize(PowerOptimizer.optimizer)),
      function("iteration", v.iteration.node.optimize(PowerOptimizer.optimizer))
    )
  }

  override def main(v: DivergingSeries)(inputVar: RefVec2, outputVar: RefVec4): String =
    s"""{
       |  int l = 0;
       |  vec2 lambda = ${inputVar.name};
       |  vec2 z = initial(lambda);
       |  for(int i = 0;i < max_iterations; i++){
       |    z = iteration(z, lambda);
       |    if(dot(z,z) > escape_radius * escape_radius)
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float(max_iterations);
       |  ${outputVar.name} = mix(color_inside, color_outside, fract);
       |}
       """.stripMargin

}
