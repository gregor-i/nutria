// package entities.imageFactory

// import entities.accumulator.Variance
// import entities.content.Content
// import entities.viewport.Transform
// import entities.accumulator._
// import entities.fractal.Fractal

// case class DynamAntiAlias(private val accus: () => Accumulator, private val limit: Double) extends Content {

//  override def apply(fractal: Fractal, trans: Transform, x_i: Int, y_i: Int): Double = {
//    val sigmaSquared = new Variance()
//    val x0 = trans.transformX(x_i, y_i)
//    val y0 = trans.transformY(x_i, y_i)
//    var rx = 0.0
//    var ry = 0.0
//    val accu = accus()
//    var n = 0
//    val g_loc = g * trans.width
//    val gg_loc = gg * trans.height
//    while (sigmaSquared.result(n) / n > limit || 2 > n) {
//      rx = (rx + g_loc) % trans.dx
//      ry = (ry + gg_loc) % trans.dy
//      val e = fractal(x0 + rx, y0 + ry)
//      accu.next(e)
//      sigmaSquared.next(e)
//      n += 1
//    }
//    accu.result(n)
//  }
// }
