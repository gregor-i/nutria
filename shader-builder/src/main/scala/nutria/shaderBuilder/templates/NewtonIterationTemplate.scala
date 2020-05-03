package nutria.shaderBuilder.templates

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.NewtonIteration
import nutria.core.languages.X
import nutria.shaderBuilder.{FloatLiteral, IntLiteral, RefVec2, RefVec4}

object NewtonIterationTemplate extends Template[NewtonIteration] {
  override def definitions(v: NewtonIteration): Seq[String] = Seq(
    constant("threshold", FloatLiteral(v.threshold.value)),
    constant("overshoot", FloatLiteral(v.overshoot.value)),
    constant("brightness_factor", FloatLiteral(v.brightnessFactor.value)),
    constant("center_x", FloatLiteral(v.center._1)),
    constant("center_y", FloatLiteral(v.center._2)),
    constant("max_iterations", IntLiteral(v.maxIterations.value)),
    function("initial", v.initial.node),
    function("f", v.function.node),
    function("f_derived", v.function.node.derive(X))
  )

  override def main(n: NewtonIteration): String =
    s"""{
       |  int l = 0;
       |  vec2 lambda = p;
       |  vec2 z = initial(lambda);
       |  vec2 fz = f(z, lambda);
       |  vec2 fz_last;
       |  for(int i = 0;i< max_iterations; i++){
       |    fz_last = fz;
       |    fz = f(z, lambda);
       |    vec2 fz_derived = f_derived(z, lambda);
       |    z -= overshoot * complex_divide(fz, fz_derived);
       |    if(length(fz) < threshold)
       |      break;
       |    l ++;
       |  }
       |
       |  if(length(fz) < threshold){
       |    float fract = 0.0;
       |    if(fz == vec2(0.0)){
       |      fract = float(l);
       |    }else{
       |      fract = float(l) + 1.0 - log(threshold / length(fz)) / log( length(fz_last) / length(fz));
       |    }
       |
       |    float H = atan(z.x - center_x, z.y - center_y) / (2.0 * 3.41);
       |    float V = exp(-fract / brightness_factor);
       |    float S = length(z);
       |
       |    return vec4(hsv2rgb(vec3(H, S, V)), 1.0);
       |  }else{
       |    return vec4(vec3(0.0), 1.0);
       |  }
       |}
       """.stripMargin
}
