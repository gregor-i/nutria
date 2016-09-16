/*package entities.fractal
import Mandelbrot._
import scala.collection.JavaConversions._

import org.apache.commons.math3.complex.Complex

class Collatz(val maxIteration: Int) extends Fractal {

  val one = Complex.ONE
  val two = new Complex(2.0, 0.0)
  val four = new Complex(4.0, 0.0)

  override def apply(x0: Double, y0: Double): Double = {
    var distance = 1e20
    /*var x = x0
    var y = y0*/
    val start = new Complex(x0, y0)

    var c = start
    for (iteration <- 0 until maxIteration) {
//      val t1 = one add (c multiply (4.0))
//      val t2 = one add (c multiply (2.0))
//      val t3 = c multiply (Math.PI) cos ()
//      c = (t1 add (t2 multiply t3)) multiply 0.25

      val t1 = one
      val t2 = c multiply 4
      val t3 = one add (c multiply 2)
      val t4 = (c multiply Math.PI).cos()
      c = (t1 add t2 subtract (t2 multiply t4)) multiply 0.25

//      val cos = (c multiply Math.PI) cos
//      val t1 = c multiply 0.25 multiply (one add cos)
//      val t2 = (c multiply (3.0/16.0) add new Complex(1.0/16.0)) multiply (one subtract cos)
//      val t3 = new Complex(3) subtract (((c multiply 2 subtract one) multiply (Math.PI/4) cos) multiply Math.sqrt(2))
//      c =t1 add (t2 multiply t3)

          val this_dist = c.abs()
      if (this_dist < distance)
        distance = this_dist
    }
    distance
//    c abs
  }

  override def toString(): String = "Collatz " + maxIteration
}
*/