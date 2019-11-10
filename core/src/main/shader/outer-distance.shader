int l = 0;
vec2 z_der = vec2(float(0), float(0));
{
   vec2 local_var_1 = vec2(float(1), float(0));
   z_der = local_var_1;
}
for(int i = 0; i < 200; i++){
  vec2 z_new = vec2(float(0), float(0));
  {
    vec2 local_var_1 = complex_product(vec2(z), vec2(z));
    vec2 local_var_2 = local_var_1+p;
    z_new = local_var_2;
  }
  vec2 z_der_new = vec2(float(0), float(0));
  {
    vec2 local_var_1 = complex_product(vec2(z_der), vec2(z));
    vec2 local_var_2 = vec2(float(2), float(0));
    vec2 local_var_3 = complex_product(vec2(local_var_1), vec2(local_var_2));
    vec2 local_var_4 = vec2(float(1), float(0));
    vec2 local_var_5 = local_var_3+local_var_4;
    z_der_new = local_var_5;
  }
  z = z_new;
  z_der = z_der_new;
  if(dot(z,z) > float(10000))
    break;
  l ++;
}

if(l == 200){
  color = vec4(vec3(0.0), 1.0);
}else{
  float z_length = length(z);
  float z_der_length = length(z_der);
  float d = 2.0 * z_length / z_der_length * log(z_length);
  color = vec4(vec3(d*1000.0), 1.0); // todo: do something smart here to calculate the factor
  //color = mix(vec4(color_shadow, 1.0), vec4(color_light, 1.0), d);
}
