int l = 0;
vec2 lambda = p;
vec2 z = initial(lambda);
vec2 fz = f(z, lambda);
vec2 fz_last;
for(int i = 0;i< max_iterations; i++){
  fz_last = fz;
  fz = f(z, lambda);
  vec2 fz_derived = f_derived(z, lambda);
  z -= overshoot * complex_divide(fz, fz_derived);
  if(length(fz) < threshold)
    break;
  l ++;
}

if(length(fz) < threshold){
  float fract = 0.0;
  if(fz == vec2(0.0)){
    fract = float(l);
  }else{
    fract = float(l) + 1.0 - log(threshold / length(fz)) / log( length(fz_last) / length(fz));
  }

  float H = atan(z.x - center_x, z.y - center_y) / (2.0 * PI);
  float V = exp(-fract / brightness_factor);
  float S = length(z);

  return vec4(hsv2rgb(vec3(H, S, V)), 1.0);
}else{
  return vec4(vec3(0.0), 1.0);
}
