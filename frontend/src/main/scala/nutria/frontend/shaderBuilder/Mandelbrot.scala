package nutria.frontend.shaderBuilder

object Mandelbrot {

  def iterations(maxIterations: Int, escapeRadiusSquared: Double)(inputVarName: String, outputVarName: String): String =
    s"""{
       |  vec2 p = $inputVarName;
       |
       |  int l = 0;
       |  vec2 c = p;
       |  vec2 z = vec2(0.0, 0.0);
       |  for(int i = 0;i< $maxIterations; i++){
       |		z = vec2(z.x*z.x - z.y*z.y + c.x, z.x*z.y * 2.0 + c.y);
       |    if(dot(z,z) > float($escapeRadiusSquared))
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float($maxIterations);
       |  $outputVarName = vec4(fract, fract, fract,1.0);
       |}
     """.stripMargin


  def shaded(maxIterations: Int, escapeRadiusSquared: Double)(inputVarName: String, outputVarName: String) =
    s"""{
       |  const float h2 = 1.5;  // height factor of the incoming light
       |  const float angle = ${45.0 / 180.0 * Math.PI};  // incoming direction of light
       |  const vec2 v = vec2(sin(angle), cos(angle));  // unit 2D vector in this direction
       |  // incoming light 3D vector = (v.re,v.im,h2)
       |
       |  vec2 c = $inputVarName;
       |  int l = 0;
       |  vec2 z = c;
       |  vec2 der1 = vec2(1.0, 0.0);
       |  for(int i = 0; i < $maxIterations; i++){
       |	  vec2 new_z = product(z,z) + c;
       |    vec2 new_der1 = product(der1, z) * 2.0 + vec2(1.0, 0.0);
       |    z = new_z;
       |    der1 = new_der1;
       |    if(dot(z,z) > float($escapeRadiusSquared))
       |      break;
       |    l ++;
       |  }
       |
       |  float fract = float(l) / float($maxIterations);
       |
       |  if(l == $maxIterations){
       |    $outputVarName = vec4(0.0, 0.0, 0.5, 1.0);
       |  }else{
       |    vec2 u = divide(z, der1);
       |    float absu = sqrt(u.x*u.x+u.y*u.y);
       |    u = u/absu;
       |    float t = u.x*v.x + u.y*v.y + h2; //  # dot product with the incoming light
       |    t = t/(1.0+h2); //  # rescale so that t does not get bigger than 1
       |    if(t<0.0) {
       |      t = 0.0;
       |    }
       |    $outputVarName = vec4(mix(vec3(0.0, 0.0, 0.0), vec3(1.0, 1.0, 1.0), t),1.0);
       |  }
       |}
       |
     """.stripMargin
}
