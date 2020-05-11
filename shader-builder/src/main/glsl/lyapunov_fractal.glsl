float x = 0.5;
float h = 0.0;

for( int i=0; i<iterations; i++ ){
  for(int i=0; i<steps_X; i++){
    x = p.x*x*(1.0-x); h += log2(abs(p.x*(1.0-2.0*x)));
  }

  for(int i=0; i<steps_Y; i++){
    x = p.y*x*(1.0-x); h += log2(abs(p.y*(1.0-2.0*x)));
  }
}

h /= float(iterations * (steps_X + steps_Y));

vec3 col = vec3(0.0);
if( h<0.0 ){
  h = abs(h);
  col = (0.5 + 0.5*sin( vec3(0.0, 0.4, 0.7) + 2.5*h )) * pow(h,0.25);
}
return vec4(col, 1.0);
