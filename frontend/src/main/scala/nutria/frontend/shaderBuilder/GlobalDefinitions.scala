package nutria.frontend.shaderBuilder

object GlobalDefinitions {
  val definitions =
    """
      |// why is this necessary?
      |#define sinh(x) 0.5*(exp(x)-(exp(-x)))
      |#define cosh(x) 0.5*(exp(x)+(exp(-x)))
      |
      |//Define complex operations
      |#define complex_product(a, b) vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x)
      |#define complex_conjugate(a) vec2(a.x,-a.y)
      |#define complex_divide(a, b) vec2(((a.x*b.x+a.y*b.y)/(b.x*b.x+b.y*b.y)),((a.y*b.x-a.x*b.y)/(b.x*b.x+b.y*b.y)))
      |
      |// see: http://mathworld.wolfram.com/ComplexExponentiation.html
      |// and: https://github.com/typelevel/spire/blob/0ee38b7abc9a42fe92a63c654498305ec80be454/core/src/main/scala/spire/math/Complex.scala#L208
      |vec2 complex_power(vec2 a, vec2 b){
      |  float length_a = length(a);
      |  float arg_a = atan(a.y, a.x);
      |  float magnitude = pow(length_a, b.x) / exp(arg_a * b.y);
      |  float angle = arg_a * b.x + log(length_a) * b.y;
      |  return magnitude * vec2(cos(angle), sin(angle));
      |}
      |
      |#define complex_exp(a) exp(a.x) * vec2(cos(a.y), sin(a.y))
      |#define complex_sin(a) vec2(sin(a.x)*cosh(a.y), cos(a.x)*sinh(a.y))
      |#define complex_cos(a) vec2(cos(a.x)*cosh(a.y), -sin(a.x)*sinh(a.y))
      |#define complex_tan(a) vec2(sin(2.0*a.x), sinh(2.0*a.y)) / (cos(2.0*a.x) + cosh(2.0*a.y))
      |#define complex_log(a) vec2(length(a), atan(a.x, a.y))
      |
      |vec3 hsv2rgb(vec3 c)
      |{
      |    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
      |    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
      |    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
      |}
    """.stripMargin
}
