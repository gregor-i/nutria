//package entities.imageFactory
//
//import entities.accumulator._
//import entities.viewport.Transform
//import entities.fractal.Fractal
//import AntiAliaseConstants._
//
//class Height(private val limit: Double) extends ImageFactory {
//
//  override def apply(fractal: Fractal, trans: Transform, x_i: Int, y_i: Int): Double = {
//    val `var` = new Variance()
//    val x0 = trans.transformX(x_i, y_i)
//    val y0 = trans.transformY(x_i, y_i)
//    val dx = trans.transformX(x_i + 1, y_i + 1) - x0
//    val dy = trans.transformY(x_i + 1, y_i + 1) - y0
//    var rx = 0.0
//    var ry = 0.0
//    var n = 0
//    do {
//      rx = (rx + g) % 1
//      ry = (ry + gg) % 1
//      `var`.next(fractal(x0 + rx * dx, y0 + ry * dy))
//      n += 1
//    } while (`var`.result(n) / n > limit || 5 > n);
//    n.toDouble
//  }
//
//  override def toString(): String = "Height " + limit
//}
