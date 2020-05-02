package nutria.shaderBuilder.templates

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.languages.Lambda
import nutria.core.{DivergingSeries, NormalMap}
import nutria.shaderBuilder._

private[templates] object NormalMapTemplate extends Template[DivergingSeries] {
  override def constants(v: DivergingSeries): Seq[String] = {
    val c = v.coloring.asInstanceOf[NormalMap]
    Seq(
      constant("max_iterations", IntLiteral(v.maxIterations.value)),
      constant("escape_radius", FloatLiteral(v.escapeRadius.value)),
      constant("angle", FloatLiteral(c.angle.value)),
      constant("color_inside", Vec4.fromRGBA(c.colorInside)),
      constant("color_light", Vec4.fromRGBA(c.colorLight)),
      constant("color_shadow", Vec4.fromRGBA(c.colorShadow)),
      constant("h2", FloatLiteral(c.h2.value))
    )
  }

  override def functions(v: DivergingSeries): Seq[String] =
    Seq(
      function("initial", v.initial.node.optimize(PowerOptimizer.optimizer)),
      function("iteration", v.iteration.node.optimize(PowerOptimizer.optimizer)),
      function("initial_derived", v.initial.node.derive(Lambda).optimize(PowerOptimizer.optimizer)),
      function("iteration_derived", DivergingSeries.deriveIteration(v).optimize(PowerOptimizer.optimizer))
    )

  override def main(v: DivergingSeries)(inputVar: RefVec2, outputVar: RefVec4): String =
    s"""{
       |  vec2 lambda = ${inputVar.name};
       |  vec2 z = initial(lambda);
       |  vec2 z_derived = initial_derived(lambda);
       |  int l = 0;
       |  for(int i = 0; i < max_iterations; i++){
       |    z_derived = iteration_derived(z, z_derived, lambda);
       |    z = iteration(z, lambda);
       |    if(dot(z,z) > escape_radius * escape_radius)
       |      break;
       |    l ++;
       |  }
       |
       |  if(l == max_iterations){
       |    ${outputVar.name} = color_inside;
       |  }else{
       |    const vec2 v = vec2(cos(angle), sin(angle));
       |    vec2 u = normalize(complex_divide(z, z_derived));
       |    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
       |    ${outputVar.name} = mix(color_shadow, color_light, t);
       |  }
       |}
    """.stripMargin

}
