  int l = 0;
  for(int i = 0;i< 200; i++){
    vec2 oldz = z;
    z = z-complex_divide(vec2(complex_product(vec2(complex_product(vec2(z), vec2(z))), vec2(z))-vec2(float(1), float(0))), vec2(complex_product(vec2(complex_product(vec2(vec2(float(2), float(0))), vec2(z))), vec2(z))))+p;
    if(length(z - oldz) < float(0.001))
      break;
    l ++;
  }

  color = vec4(vec3(float(l) / float(200)), 1.0);