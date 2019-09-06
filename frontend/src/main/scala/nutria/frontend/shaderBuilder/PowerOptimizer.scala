package nutria.frontend.shaderBuilder

import mathParser.{BinaryNode, ConstantNode, Optimizer}
import mathParser.algebra.{Power, SpireBinaryOperator, SpireLanguage, SpireUnitaryOperator}
import nutria.core.languages.CNode
import spire.math.Complex
import mathParser.algebra.SpireLanguage.syntax.EnrichNode
import mathParser.implicits._

object PowerOptimizer {
  def optimizer[V]: Optimizer[SpireUnitaryOperator, SpireBinaryOperator, Complex[Double], V] =
   new Optimizer[SpireUnitaryOperator, SpireBinaryOperator, Complex[Double], V] {

     val powerReducer: PartialFunction[CNode[V], CNode[V]] = {
       case BinaryNode(Power, left, ConstantNode(Complex(n, 0.0))) if n % 1.0 == 0.0 && n > 1.0 =>
         val i = n.toInt
         if (i % 2 == 0)
           (left ^ ConstantNode(i / 2)) * (left ^ ConstantNode(i / 2))
         else
           (left ^ ConstantNode(i / 2)) * (left ^ ConstantNode(i / 2)) * left
     }

     def rules: List[PartialFunction[CNode[V], CNode[V]]] =
       SpireLanguage.spireOptimizer[Complex[Double], V].rules :+ powerReducer
   }
}
