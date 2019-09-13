// https://en.wikipedia.org/wiki/Mandelbrot_set#Exterior_distance_estimation

// distance function (provided derivitive)

  int l = 0;
  vec2 z_der = vec2(float(1), float(0));
  for(int i = 0; i < 500; i++){
    vec2 z_new = complex_product(vec2(z), vec2(z))+p;
    vec2 z_der_new = complex_product(vec2(complex_product(vec2(z_der), vec2(z))), vec2(vec2(float(2), float(0))))+vec2(float(1), float(0));
    z = z_new;
    z_der = z_der_new;
    if(dot(z,z) > float(100))
      break;
    l ++;
  }

if(l != 500){
    float d = length(z) * log(length(z)) / length(z_der);
    color = mix(vec4(0.0, 0.0, 0.0, 1.0), vec4(1.0, 1.0, 1.0, 1.0), d*100000.0);
}else{
    color = vec4(0.0, 0.0, 0.0, 1.0);
}
