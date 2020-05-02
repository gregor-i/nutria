package nutria.shaderBuilder.templates

import mathParser.Syntax._
import mathParser.implicits._
import nutria.core.NewtonIteration
import nutria.core.languages.X
import nutria.shaderBuilder.{FloatLiteral, IntLiteral, RefVec2, RefVec4}

object NewtonIterationTemplate extends Template[NewtonIteration] {
  override def constants(v: NewtonIteration): Seq[String] = Seq(
    constant("threshold", FloatLiteral(v.threshold.value)),
    constant("overshoot", FloatLiteral(v.overshoot.value)),
    constant("max_iterations", IntLiteral(v.maxIterations.value))
  )

  override def functions(v: NewtonIteration): Seq[String] =
    Seq(
      function("initial", v.initial.node),
      function("f", v.function.node),
      function("f_derived", v.function.node.derive(X))
    )

  override def main(n: NewtonIteration)(inputVar: RefVec2, outputVar: RefVec4): String =
    s"""{
       |  int l = 0;
       |  vec2 lambda = ${inputVar.name};
       |  vec2 z = initial(lambda);
       |  vec2 fz = f(z, lambda);
       |  vec2 fz_last;
       |  for(int i = 0;i< max_iterations; i++){
       |    fz_last = fz;
       |    fz = f(z, lambda);
       |    vec2 fz_derived = f_derived(z, lambda);
       |    z -= ${FloatLiteral(n.overshoot.value.toFloat).toCode} * complex_divide(fz, fz_derived);
       |    if(length(fz) < ${FloatLiteral(n.threshold.value.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |  if(length(fz) < ${FloatLiteral(n.threshold.value.toFloat).toCode}){
       |    float fract = 0.0;
       |    if(fz == vec2(0.0)){
       |      fract = float(l);
       |    }else{
       |      fract = float(l) + 1.0 - log(${n.threshold} / length(fz)) / log( length(fz_last) / length(fz));
       |    }
       |
       |    float H = atan(z.x - ${FloatLiteral(n.center._1.toFloat).toCode}, z.y - ${FloatLiteral(
         n.center._2.toFloat
       ).toCode}) / float(${2 * Math.PI});
       |    float V = exp(-fract / ${FloatLiteral(n.brightnessFactor.value.toFloat).toCode});
       |    float S = length(z);
       |
       |    ${outputVar.name} = vec4(hsv2rgb(vec3(H, S, V)), 1.0);
       |  }else{
       |    ${outputVar.name} = vec4(vec3(0.0), 1.0);
       |  }
       |}
       """.stripMargin
}
