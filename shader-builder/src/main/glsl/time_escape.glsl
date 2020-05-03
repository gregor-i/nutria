int l = 0;
vec2 lambda = p;
vec2 z = initial(lambda);
for(int i = 0;i < max_iterations; i++){
  z = iteration(z, lambda);
  if(dot(z,z) > escape_radius * escape_radius)
    break;
  l ++;
}

float fract = float(l) / float(max_iterations);
return mix(color_inside, color_outside, fract);
