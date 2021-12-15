package nutria.shaderBuilder

import mathParser.complex.ComplexLanguage.syntax.EnrichNode
import mathParser.complex._
import mathParser.{BinaryNode, ConstantNode, Optimizer}

private[shaderBuilder] object PowerOptimizer {
  def optimizer[V]: Optimizer[ComplexUnitaryOperator, ComplexBinaryOperator, Complex, V] =
    new Optimizer[ComplexUnitaryOperator, ComplexBinaryOperator, Complex, V] {
      val powerReducer: PartialFunction[ComplexNode[V], ComplexNode[V]] = {
        case BinaryNode(Power, left, ConstantNode(Complex(n, 0.0))) if n % 1.0 == 0.0 && n > 1.0 && n <= 1024 =>
          val i = n.toInt
          if (i % 2 == 0)
            (left ^ ConstantNode(Complex(i / 2, 0.0))) * (left ^ ConstantNode(Complex(i / 2, 0.0)))
          else
            (left ^ ConstantNode(Complex(i / 2, 0.0))) * (left ^ ConstantNode(Complex(i / 2, 0.0))) * left
      }

      def rules: List[PartialFunction[ComplexNode[V], ComplexNode[V]]] =
        ComplexLanguage.complexOptimizer[V].rules :+ powerReducer
    }
}
