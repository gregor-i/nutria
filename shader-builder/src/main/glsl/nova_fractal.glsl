vec2 lambda = p;
vec2 z = p;
int l = 0;
for(int i = 0; i<max_iterations; i++){
  vec2 delta = complex_divide(iteration(z, lambda), iteration_derived(z, lambda)) + p;
  z -= delta;
  if(length(delta) < float(0.001))
     break;
  l ++;
}

return vec4(vec3(float(max_iterations - l) / float(max_iterations)), 1.0);
