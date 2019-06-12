package nutria.frontend.shaderBuilder

import nutria.frontend.Ui

object Consumer {

  def iterations(maxIterations: Int, escapeRadiusSquared: Double, iteration: Iteration)(inputVar: RefVec2, outputVar: RefVec4): String =
    s"""{
       |  int l = 0;
       |  ${Iteration.initial(iteration)(RefVec2("z"), inputVar)}
       |  for(int i = 0;i< $maxIterations; i++){
       |		${Iteration.step(iteration)(RefVec2("z"), inputVar)}
       |    if(dot(z,z) > float($escapeRadiusSquared))
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float($maxIterations);
       |  ${outputVar.name} = vec4(fract, fract, fract,1.0);
       |}
     """.stripMargin


  def shaded(maxIterations: Int, escapeRadiusSquared: Double, iteration: DeriveableIteration)(inputVar: RefVec2, outputVar: RefVec4) = {
    val h2 = 2.0
    val angle = 45.0 / 180.0 * Math.PI
    val vx = Math.sin(angle)
    val vy = Math.sin(angle)
    // incoming light 3D vector = (v.re,v.im,h2)
    s"""{
       |  int l = 0;
       |	${DeriveableIteration.initial(iteration)(RefVec2("z"), inputVar)}
       |  for(int i = 0; i < $maxIterations; i++){
       |	  ${DeriveableIteration.step(iteration)(RefVec2("z"), inputVar)}
       |    if(dot(z,z) > float($escapeRadiusSquared))
       |      break;
       |    l ++;
       |  }
       |
       |  if(l == $maxIterations){
       |    ${outputVar.name} = vec4(0.0, 0.0, 0.25, 1.0);
       |  }else{
       |    const float h2 = float($h2);
       |    const vec2 v = vec2(float($vx), float($vy));
       |    vec2 u = normalize(divide(z, z_der));
       |    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
       |    ${outputVar.name} = mix(vec4(0.0, 0.0, 0.0, 1.0), vec4(1.0, 1.0, 1.0, 1.0), t);
       |  }
       |}
     """.stripMargin
  }

  def newtonIteration(maxIterations: Int, threshold: Double, fn: String)(inputVar: RefVec2, outputVar: RefVec4): String = {
    val node = Parser.lang.parse(fn).get
    val derived = Parser.lang.derive(node)('x)

    val z = RefVec2("z")
    val fzlast = RefVec2("fzlast")
    val fz = RefVec2("fz")
    val fderz = RefVec2("fderz")
    s"""{
       |  int l = 0;
       |  ${Iteration.newtonInitial(node, derived)(z, RefVec2("p"))}
       |  for(int i = 0;i< $maxIterations; i++){
       |    ${Iteration.newtonStep(node, derived)(z, RefVec2("p"))}
       |    if(length(${fz.name}) < ${FloatLiteral(threshold.toFloat).toCode})
       |      break;
       |    l ++;
       |  }
       |
       |
       |  float fract = 0.0;
       |  if(fz == ${WebGlType.zero[WebGlTypeVec2.type ].toCode}){
       |    fract = float(l - 1);
       |  }else{
       |    fract = float(l) - log(${threshold} / length(${fz.name})) / log( length(${fzlast.name}) / length(${fz.name}));
       |  }
       |
       |  float H = atan(z.x, z.y) / float(${2*Math.PI}) + 0.5;
       |  float S = exp(-fract / 25.0);
       |  float V = S;
       |
       |  ${outputVar.name} = vec4(hsv2rgb(vec3(H, S, V)), 1.0);
       |}
     """.stripMargin
  }
}
