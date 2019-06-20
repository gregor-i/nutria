package nutria.frontend.shaderBuilder

object GlobalDefintions {
  val definitions =
    """
      |//Define complex operations
      |#define complex_product(a, b) vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x)
      |#define complex_conjugate(a) vec2(a.x,-a.y)
      |#define complex_divide(a, b) vec2(((a.x*b.x+a.y*b.y)/(b.x*b.x+b.y*b.y)),((a.y*b.x-a.x*b.y)/(b.x*b.x+b.y*b.y)))
      |#define complex_exp(a) exp(a.x) * vec2(cos(a.y), sin(a.y))
      |#define complex_sq(a) complex_product(a, a)
      |
    """.stripMargin

}
