// why is this necessary?
#define sinh(x) 0.5*(exp(x)-(exp(-x)))
#define cosh(x) 0.5*(exp(x)+(exp(-x)))

// inspired by spire implementation of complex number:
// see: https://github.com/typelevel/spire/blob/master/core/src/main/scala/spire/math/Complex.scala

vec2 complex_product(vec2 a, vec2 b){
 return vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x);
}

vec2 complex_divide(a, b){
  return vec2(((a.x*b.x+a.y*b.y)/(b.x*b.x+b.y*b.y)),((a.y*b.x-a.x*b.y)/(b.x*b.x+b.y*b.y)));
}

// see: http://mathworld.wolfram.com/ComplexExponentiation.html
vec2 complex_power(vec2 a, vec2 b){
  float length_a = length(a);
  float arg_a = atan(a.y, a.x);
  float magnitude = pow(length_a, b.x) / exp(arg_a * b.y);
  float angle = arg_a * b.x + log(length_a) * b.y;
  return magnitude * vec2(cos(angle), sin(angle));
}

// exp(a+ci) = (exp(a) * cos(c)) + (exp(a) * sin(c))i
vec2 complex_exp(vec2 z){
 return exp(z.x) * vec2(cos(z.y), sin(z.y));
}

vec2 complex_log(vec2 a){
  return vec2(length(a), atan(a.x, a.y));
}

// https://en.wikipedia.org/wiki/Methods_of_computing_square_roots#Negative_or_complex_square
vec2 complex_sqrt(vec2 z){
  float length_z = length(z);
  float b = sqrt((length_z + z.x) / 2.0);
  float c = sqrt((length_z - z.x) / 2.0);
  if (z.y < 0.0)
    return vec2(b, -c);
  else
    return vec2(b, c);
}

// sin(a+ci) = (sin(a) * cosh(c)) + (cos(a) * sinh(c))i
vec2 complex_sin(vec2 a){
 return vec2(sin(a.x)*cosh(a.y), cos(a.x)*sinh(a.y));
}

// cos(a+ci) = (cos(a) * cosh(c)) - (sin(a) * sinh(c))i
vec2 complex_cos(vec2 a){
  return vec2(cos(a.x)*cosh(a.y), -sin(a.x)*sinh(a.y));
}

// tan(a+ci) = (sin(a+a) + sinh(c+c)i) / (cos(a+a) + cosh(c+c))
vec2 complex_tan(vec2 a){
 return vec2(sin(2.0*a.x), sinh(2.0*a.y)) / (cos(2.0*a.x) + cosh(2.0*a.y));
}

// acos(z) = -i*(log(z + i*(sqrt(1 - z*z))))
vec2 complex_acos(vec2 z){
  vec2 z2 = complex_product(z, z);
  vec2 s = complex_sqrt(vec2(1.0 - z2.x, -z2.y));
  vec2 l = complex_log(vec2(z.x + s.y, z.y + s.x));
  return vec2(l.x, -l.y);
}

// asin(z) = -i*(log(sqrt(1 - z*z) + i*z))
vec2 complex_asin(vec2 z){
  vec2 z2 = complex_product(z, z);
  vec2 s = complex_sqrt(vec2(1.0 - z2.x, -z2.y));
  vec2 l = complex_log(vec2(s.x + -z.y, s.y + z.x));
  return vec2(l.x, -l.y);
}

// atan(z) = (i/2) log((i + z)/(i - z))
vec2 complex_atan(vec2 z){
  vec2 n = vec2(z.x, z.y + 1.0);
  vec2 d = vec2(-z.x, 1.0 - z.y);
  vec2 l = complex_log(complex_divide(n, d));
  return vec2(l.y / -2.0, l.x / 2.0);
}

// sinh(a+ci) = (sinh(a) * cos(c)) + (cosh(a) * sin(c))i
vec2 complex_sinh(vec2 z){
  return vec2(sinh(z.x) * cos(z.y), cosh(z.x) * sin(z.y));
}

// cosh(a+ci) = (cosh(a) * cos(c)) + (sinh(a) * sin(c))i
vec2 complex_cosh(vec2 z){
  return vec2(cosh(z.x) * cos(z.y), sinh(z.x) * sin(z.y));
}

// tanh(a+ci) = (sinh(a+a) + sin(c+c)i) / (cosh(a+a) + cos(c+c))
vec2 complex_tanh(vec2 z){
  float r2 = z.x + z.x;
  float i2 = z.y + z.y;
  float d = cos(r2) + cosh(i2);
  return vec2(sinh(r2) / d, sin(i2) / d);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}