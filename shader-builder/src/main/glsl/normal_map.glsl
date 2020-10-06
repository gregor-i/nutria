vec4 pixel(const in vec2 p) {
  vec2 lambda = p;
  vec2 z = initial(lambda);
  vec2 z_derived = initial_derived(lambda);
  int l = 0;
  for(int i = 0; i < max_iterations; i++){
    z_derived = iteration_derived(z, z_derived, lambda);
    z = iteration(z, lambda);
    if(dot(z,z) > escape_radius * escape_radius)
      break;
    l ++;
  }
  
  if(l == max_iterations){
    return color_inside;
  }else{
    const vec2 v = vec2(cos(angle), sin(angle));
    vec2 u = normalize(complex_divide(z, z_derived));
    float t = max((dot(u, v) + h2) / (1.0 + h2), 0.0);
    return mix(color_shadow, color_light, t);
  }
}