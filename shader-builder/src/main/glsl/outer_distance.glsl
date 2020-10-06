vec4 pixel(const in vec2 p) {
  float pixel_distance = length((u_view_A + u_view_B) / u_resolution);
  int l = 0;
  vec2 lambda = p;
  vec2 z = initial(lambda);
  vec2 z_derived = initial_derived(lambda);
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
    float z_length = length(z);
    float z_der_length = length(z_derived);
    float d = distance_factor / pixel_distance * 2.0 * z_length / z_der_length * log(z_length);
    return mix(color_near, color_far, d);
  }
}