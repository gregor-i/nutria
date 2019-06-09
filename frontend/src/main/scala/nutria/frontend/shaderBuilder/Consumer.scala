package nutria.frontend.shaderBuilder

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
}
