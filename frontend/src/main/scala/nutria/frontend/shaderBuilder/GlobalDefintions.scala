package nutria.frontend.shaderBuilder

object GlobalDefintions {
  val definitions =
    """
      |// why is this necessary?
      |#define sinh(x) 0.5*(exp(x)-(exp(-x)))
      |#define cosh(x) 0.5*(exp(x)+(exp(-x)))
      |
      |
      |//Define complex operations
      |#define complex_product(a, b) vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x)
      |#define complex_conjugate(a) vec2(a.x,-a.y)
      |#define complex_divide(a, b) vec2(((a.x*b.x+a.y*b.y)/(b.x*b.x+b.y*b.y)),((a.y*b.x-a.x*b.y)/(b.x*b.x+b.y*b.y)))
      |#define complex_power(a, b) vec2(pow(length(a), b.x) / exp(atan(a.x, a.y) * b.y), atan(a.x, a.y)*b.x + log(length(a)) * b.y)
      |
      |#define complex_exp(a) exp(a.x) * vec2(cos(a.y), sin(a.y))
      |#define complex_sq(a) complex_product(a, a)
      |#define complex_sin(a) vec2(sin(a.x)*cosh(a.y), cos(a.x)*sinh(a.y))
      |#define complex_cos(a) vec2(cos(a.x)*cosh(a.y), -sin(a.x)*sinh(a.y))
      |#define complex_tan(a) vec2(sin(2.0*a.x), sinh(2.0*a.y)) / (cos(2.0*a.x) + cosh(2.0*a.y))
      |#define complex_log(a) vec2(length(a), atan(a.x, a.y))
      |
    """.stripMargin


  import spire.math.Complex
}
