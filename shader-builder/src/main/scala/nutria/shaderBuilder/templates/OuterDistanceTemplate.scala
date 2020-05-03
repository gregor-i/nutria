package nutria.shaderBuilder.templates

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.languages.Lambda
import nutria.core.{DivergingSeries, OuterDistance}
import nutria.shaderBuilder._

private[templates] object OuterDistanceTemplate extends Template[DivergingSeries] {
  override def definitions(v: DivergingSeries): Seq[String] = {
    val c = v.coloring.asInstanceOf[OuterDistance]
    Seq(
      constant("max_iterations", IntLiteral(v.maxIterations.value)),
      constant("escape_radius", FloatLiteral(v.escapeRadius.value)),
      constant("distance_factor", FloatLiteral(c.distanceFactor.value)),
      constant("color_inside", Vec4.fromRGBA(c.colorInside)),
      constant("color_far", Vec4.fromRGBA(c.colorFar)),
      constant("color_near", Vec4.fromRGBA(c.colorNear)),
      function("initial", v.initial.node.optimize(PowerOptimizer.optimizer)),
      function("iteration", v.iteration.node.optimize(PowerOptimizer.optimizer)),
      function("initial_derived", v.initial.node.derive(Lambda).optimize(PowerOptimizer.optimizer)),
      function("iteration_derived", DivergingSeries.deriveIteration(v).optimize(PowerOptimizer.optimizer))
    )
  }

  override def main(v: DivergingSeries)(inputVar: RefVec2, outputVar: RefVec4): String =
    s"""{
       |  float pixel_distance = length((u_view_A + u_view_B) / u_resolution);
       |  int l = 0;
       |  vec2 lambda = ${inputVar.name};
       |  vec2 z = initial(lambda);
       |  vec2 z_derived = initial_derived(lambda);
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
       |    float z_length = length(z);
       |    float z_der_length = length(z_derived);
       |    float d = distance_factor / pixel_distance * 2.0 * z_length / z_der_length * log(z_length);
       |    ${outputVar.name} = mix(color_near, color_far, d);
       |  }
       |}
    """.stripMargin

}
