vec4 pixel(const in vec2 p) {
  #define calc_tri_area(A, B, C) float((((A.x - C.x) * (B.y - C.y)) - ((B.x - C.x) * (A.y - C.y))) / 2.0)
  
  const float pi = 3.141;
  
  vec2 A = vec2(size*sin( 60.0/180.0*pi), size*cos( 60.0/180.0*pi));
  vec2 B = vec2(size*sin(180.0/180.0*pi), size*cos(180.0/180.0*pi));
  vec2 C = vec2(size*sin(300.0/180.0*pi), size*cos(300.0/180.0*pi));
  
  float area = calc_tri_area(A, B, C);
  vec3 bary = vec3(calc_tri_area(B, C, p), calc_tri_area(C, A, p), calc_tri_area(A, B, p));
  
  if(bary.x > 0.0 || bary.y > 0.0 || bary.z > 0.0){
    // outside
    return color_outside;
  }else{
    // inside
    for(int i = 0; i < iterations; i++){
      bary = vec3(calc_tri_area(B, C, p), calc_tri_area(C, A, p), calc_tri_area(A, B, p));
      // inside
      if(bary.x / area > 0.5){
        B = 0.5 * (B + A);
        C = 0.5 * (C + A);
      } else if(bary.y / area > 0.5){
        // in area near B
        A = 0.5 * (A + B);
        C = 0.5 * (C + B);
      }else if(bary.z / area > 0.5){
        // in area near C
        A = 0.5 * (A + C);
        B = 0.5 * (B + C);
      }else{
        // in the middle triangle
        return mix(color_outside, color_inside, float(i)/float(iterations));
      }
    area /= 4.0;
    }
    return color_inside;
  }
}