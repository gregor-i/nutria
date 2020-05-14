int l = 0;
vec2 lambda = p;
vec2 z = initial(lambda);
float dist = length(z - floor(z + vec2(0.5)));
for(int i = 0;i < max_iterations; i++){
  z = iteration(z, lambda);
  dist = min(dist, length(z - floor(z+ vec2(0.5))));
  if(dot(z,z) > escape_radius * escape_radius)
    break;
  l ++;
}

float fract = dist / sqrt(0.5);
return mix(color_near_gaussian, color_far_gaussian, fract);
