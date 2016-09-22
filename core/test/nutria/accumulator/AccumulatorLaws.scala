//package nutria.accumulator
//
//import org.specs2.{ScalaCheck, Specification}
//
//import scala.reflect.ClassTag
//import scala.util.Random
//
//case class AccumulatorLaws[A <: Accumulator : ClassTag](accumulator:A)(implicit ct:ClassTag[A]) extends Specification with ScalaCheck {
//    def is = s"Accumulator Laws for ${ct.getClass.getSimpleName}" ^ s2"""
//       Accumulators are independant of the order: $orderIndependant
//      """
//
//  def compareInclusiveNaN(a:Double, b:Double): Boolean = (a == a, b == b) match {
//    case (false, false) => true
//    case (true, _) | (_, true) => false
//    case _ => a == b
//  }
//
//  def orderIndependant = prop{(ds: Seq[Double]) => compareInclusiveNaN(accumulator(ds) , accumulator(Random.shuffle(ds)))}
//}
