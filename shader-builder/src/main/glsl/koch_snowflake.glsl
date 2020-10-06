vec4 pixel(const in vec2 p) {
  vec2 z = p;
  if(z.x < 0.0 || z.x > 1.0 || z.y < -0.5 || z.y > 0.5){
    return color_outside;
  }else{
    z = abs(fract(z)-0.5);
    for(int i = 0; i < iterations ; i++){
      z += vec2(z.y*1.735, -z.x*1.735);
      z.x = abs(z.x)-0.58;
      z = -vec2(-z.y, z.x)*.865;
    }
  
    if(z.x > 0.0){
      return color_outside;
    }else{
      return color_inside;
    }
  }
}